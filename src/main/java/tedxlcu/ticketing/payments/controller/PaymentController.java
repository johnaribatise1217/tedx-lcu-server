package tedxlcu.ticketing.payments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tedxlcu.ticketing.payments.Request.InitializePaymentRequest;
import tedxlcu.ticketing.payments.Request.createTicketBookingReq;
import tedxlcu.ticketing.payments.Response.ApiResponse;
import tedxlcu.ticketing.payments.model.TicketBooking;
import tedxlcu.ticketing.payments.service.EmailService;
import tedxlcu.ticketing.payments.service.Payment.PaymentService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/payment")
public class PaymentController {
  @Autowired
  private PaymentService paymentService;
  @Autowired
  private EmailService emailService;

  @PostMapping("/initialize")
  public ResponseEntity<ApiResponse> 
  initializePayment(@RequestBody InitializePaymentRequest request) throws Exception{
    try {
      String authorizationUrl = paymentService.initiatePayment(request);
      return ResponseEntity.status(
        HttpStatus.OK
      ).body(new ApiResponse(true, "200", "init", authorizationUrl));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
      body(new ApiResponse(false, "500", e.getMessage(), null));
    }
  }

  @PostMapping("/callback")
  public ResponseEntity<ApiResponse> 
  handleVerificationCallback(
    @RequestParam String trxref,
    @RequestParam String ticketId,
    @RequestBody createTicketBookingReq request) throws Exception 
  {
    try {
      TicketBooking newTicketBooking = paymentService.verifyPayment(trxref, ticketId, request);
      emailService.sendTicketMail(newTicketBooking);
      return ResponseEntity.ok().body(
        new ApiResponse(true, "200", "Ticket booking verified successfully , check your mail for ticket QR code , do not delete the MAIL.", newTicketBooking)
      );
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
      body(new ApiResponse(false, "500", e.getMessage(), null));
    }
  }
}
