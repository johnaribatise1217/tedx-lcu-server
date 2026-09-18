package tedxlcu.ticketing.payments.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class TicketCard {
  private String id;
  private String name; // e.g., "Standard", "Circle", "Pulse"
  private String ticketType;
  private int price; // e.g., 5000, 10000, 15000 (in kobo for Paystack: multiply by 100)
  private int availableQuantity;
  private int quantitySold;
  private String ticketDescription;
}