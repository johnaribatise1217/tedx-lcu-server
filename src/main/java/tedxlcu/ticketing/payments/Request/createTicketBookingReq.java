package tedxlcu.ticketing.payments.Request;

import lombok.Data;

@Data
public class createTicketBookingReq {
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String institution;
  private String gender; // From screenshot form
  private int amount;
  private int quantity;
}
