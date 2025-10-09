package tedxlcu.ticketing.payments.service.Payment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import tedxlcu.ticketing.payments.Exception.ResourceNotFoundException;
import tedxlcu.ticketing.payments.Request.InitializePaymentRequest;
import tedxlcu.ticketing.payments.Request.createTicketBookingReq;
import tedxlcu.ticketing.payments.model.DiscountWindow;
import tedxlcu.ticketing.payments.model.TicketBooking;
import tedxlcu.ticketing.payments.model.Tickets;
import tedxlcu.ticketing.payments.repository.DiscountRepository;
import tedxlcu.ticketing.payments.repository.TicketBookingRepository;
import tedxlcu.ticketing.payments.repository.TicketRepository;
import tedxlcu.ticketing.payments.service.Discount.IDiscountService;
import tedxlcu.ticketing.payments.service.Tickets.TicketsService;

@Service
public class PaymentService {
  @Value("${paystack.secret-key-prod}")
  private String secretKey;

  @Value("${paystack.initialize-url}")
  private String initUrl;

  @Value("${paystack.verify-url}")
  private String verifyUrl;

  @Value("${admin.url}")
  private String adminUrl;

  @Value("${frontend.url}")
  private String frontendUrl;

  @Autowired
  private MongoTemplate mongoTemplate;
  @Autowired
  private TicketBookingRepository bookingRepository;
  @Autowired
  private DiscountRepository discountRepository;
  @Autowired
  private TicketRepository ticketRepository;
  @Autowired
  private TicketsService ticketsService;
  @Autowired
  private IDiscountService discountService;

  //initialize payment
  public String initiatePayment(InitializePaymentRequest request) throws Exception {
    Tickets exisTicket = ticketRepository.findById(request.getTicketId()).orElseThrow(
        () -> new ResourceNotFoundException("request cannot be found")
    );

    if (exisTicket.getAvailableQuantity() <= 0) {
        throw new Exception("Oops, Ticket Sold out!");
    }

    // Base amount in Naira
    double baseAmountNaira = exisTicket.getPrice() * request.getQuantity();

    // Apply discount (if any) in Naira first
    String discountCode = null;
    int discountPercentage = 0;
    boolean isDiscount = false;

    try {
        discountCode = request.getDiscountCode();
    } catch (Exception ignore) {}

    if (discountCode != null && !discountCode.isBlank()) {
        DiscountWindow w = discountService.validateCode(discountCode);
        discountPercentage = w.getPercentage();
        int pct = Math.max(0, Math.min(100, discountPercentage));
        baseAmountNaira = baseAmountNaira * (100 - pct) / 100.0;
        isDiscount = true;
    }

    // ✅ Add Paystack charges: 1.5% of amount + ₦100
    double paystackFee = (baseAmountNaira * 0.015) + 150;
    double totalAmountNaira = baseAmountNaira + paystackFee;

    // Convert to Kobo
    int amountToBePaidKobo = (int) Math.round(totalAmountNaira * 100);

    String uniqueRef = "TEDX2025_" + UUID.randomUUID().toString();

    Map<String, Object> payload = new HashMap<>();
    payload.put("email", request.getEmail());
    payload.put("amount", amountToBePaidKobo);
    payload.put("currency", "NGN");
    payload.put("reference", uniqueRef);
    payload.put("callback_url", frontendUrl + "/tickets/payments/success?ticketId=" + request.getTicketId());

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("cancel_action", frontendUrl + "/tickets");
    metadata.put("discount_percentage", discountPercentage);
    metadata.put("discount_code", discountCode);
    metadata.put("isDiscount", isDiscount);
    metadata.put("base_amount", baseAmountNaira);
    metadata.put("paystack_fee", paystackFee);

    payload.put("metadata", metadata);

    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpPost newPost = new HttpPost(initUrl);
        newPost.setHeader("Authorization", "Bearer " + secretKey);
        newPost.setHeader("Content-Type", "application/json");
        newPost.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(payload)));

        HttpResponse response = client.execute(newPost);
        String json = EntityUtils.toString(response.getEntity());
        Map<?, ?> resMap = new ObjectMapper().readValue(json, Map.class);

        if ((boolean) resMap.get("status")) {
            return (String) ((Map<?, ?>) resMap.get("data")).get("authorization_url");
        } else {
            throw new Exception("Init failed: " + resMap.get("message"));
        }
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        throw new Exception("Failed to initiate payment: " + e.getMessage());
    }
}


  public TicketBooking verifyPayment(String reference , String ticketId, createTicketBookingReq request ) throws Exception{
    try (CloseableHttpClient client = HttpClients.createDefault()){
      HttpGet get = new HttpGet(verifyUrl + reference);
      get.setHeader("Authorization", "Bearer " + secretKey);

      HttpResponse response = client.execute(get);
      String json = EntityUtils.toString(response.getEntity());
      Map<?, ?> resMap = new ObjectMapper().readValue(json, Map.class);

      if ( 
        (boolean) 
        resMap.get("status") && 
        "success".equals(((Map<?, ?>) resMap.get("data")).get("status"))
        ) 
      {
        Tickets exisTicket = ticketRepository.findById(ticketId).orElseThrow(
          () -> new ResourceNotFoundException("ticket cannot be found")
        );
        TicketBooking newTicketBooking = ticketsService.creatTicketBooking(request, reference, ticketId);
        newTicketBooking.setTicketName(exisTicket.getName());
        newTicketBooking.setQrCodeUrl(adminUrl + "/admin/verify/" + newTicketBooking.getId());
        bookingRepository.save(newTicketBooking);

        Map<?,?> data = (Map<?,?>) resMap.get("data");
        Map<?,?> metadata = null;
        if (data != null && data.get("metadata") instanceof Map) {
          metadata = (Map<?,?>) data.get("metadata");
        }

        Integer amountFromPaystackKobo = null;
        if (data != null && data.get("base_amount") != null) {
          try {
            amountFromPaystackKobo = Integer.parseInt(String.valueOf(data.get("base_amount")));
          } catch (NumberFormatException ignore) {}
        }

        if (amountFromPaystackKobo != null) {
          newTicketBooking.setAmountPaid(amountFromPaystackKobo);
        }

        boolean isDiscount = false;
        int discountPercentage = 0;
        String discountCode = null;
        if (metadata != null) {
          Object isDiscObj = metadata.get("isDiscount");
          if (isDiscObj != null) {
            isDiscount = Boolean.parseBoolean(String.valueOf(isDiscObj));
            newTicketBooking.setDiscount(isDiscount);
          }
          Object dpObj = metadata.get("discount_percentage");
          if (dpObj != null) {
            try {
              discountPercentage = Integer.parseInt(String.valueOf(dpObj));
              newTicketBooking.setDiscountPercentage(discountPercentage);
            } catch (NumberFormatException ignore) {}
          }
          Object dcObj = metadata.get("discount_code");
          if (dcObj != null) {
            String dcStr = String.valueOf(dcObj).trim();
            // skip literal "null" or blank strings
            if (!dcStr.isBlank() && !dcStr.equalsIgnoreCase("null")) {
              discountCode = dcStr;
              newTicketBooking.setDiscountCode(discountCode);
              discountService.findByCode(discountCode).ifPresent(w -> {
                w.setTimesUsed(w.getTimesUsed() + 1);
                discountRepository.save(w);
              });
            }
          }
        }
        bookingRepository.save(newTicketBooking);

        Query query = new Query(Criteria.where("id")
        .is(exisTicket.getId()).and("availableQuantity").gt(0));
        Update update = new Update().inc("availableQuantity", -request.getQuantity());
        mongoTemplate.findAndModify(query, update, Tickets.class);

        return newTicketBooking;
      } else {
        throw new Exception("Verify failed: " + resMap.get("message"));
      }
    } catch (Exception e) {
      throw new Exception("Failed to initiate payment: " + e.getMessage());
    }
  }
}
