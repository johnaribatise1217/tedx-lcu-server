package tedxlcu.ticketing.payments.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tedxlcu.ticketing.payments.model.TicketBooking;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class TicketAdminDetails {
  private List<TicketCard> tickets;
  private int numVerifiedBookings;
  private int numUnverifiedBookings;
  private int totalTicketsSold;
  private List<TicketBooking> bookings;
  private int numTicketsSoldByDiscount;
  
  private int totalAmount;
}