package tedxlcu.ticketing.payments.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="ticket_bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketBooking {
  @Id
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String institution;
  private String gender; // From screenshot form
  private String ticketTypeId; // Reference to TicketType
  private String ticketName;
  private String transactionReference; // From Paystack
  private int ticketQuantity;
  private int amountPaid;
  private String faculty;
  private String courseOfStudy;
  private boolean paid;
  private boolean isVerified;
  private String qrCodeUrl;
  
  private boolean isDiscount;
  private String discountCode;
  private int discountPercentage;
  
  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  private String verifiedBy;
}
