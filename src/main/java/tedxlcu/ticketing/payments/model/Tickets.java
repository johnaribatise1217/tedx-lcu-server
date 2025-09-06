package tedxlcu.ticketing.payments.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection= "tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tickets {
  @Id
  private String id;
  private String name; // e.g., "Standard", "Circle", "Pulse"
  private String ticketType;
  private int price; // e.g., 5000, 10000, 15000 (in kobo for Paystack: multiply by 100)
  private int totalQuantity;
  private int availableQuantity;
  private String ticketDescription;
  private List<String> benefits;
}
