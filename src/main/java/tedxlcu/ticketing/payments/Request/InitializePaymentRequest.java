package tedxlcu.ticketing.payments.Request;

import lombok.Data;

@Data
public class InitializePaymentRequest {
  private String ticketId;
  private int quantity;
  private String email;
}
