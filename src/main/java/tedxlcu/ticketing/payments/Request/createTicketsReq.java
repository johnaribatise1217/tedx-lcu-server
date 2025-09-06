package tedxlcu.ticketing.payments.Request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class createTicketsReq {
  private String ticketName;
  private String ticketType;
  private int price;
  private int totalQuantity;
  private String ticketDescription;
  private List<String> benefits;
}
