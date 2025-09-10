package tedxlcu.ticketing.payments.Mapper;

import java.util.List;

import tedxlcu.ticketing.payments.DTO.TicketCard;
import tedxlcu.ticketing.payments.model.Tickets;

public class DtoMapper {

  private static TicketCard mapToTicketCard(Tickets ticket) {
    return new TicketCard(
      ticket.getId(), 
      ticket.getName(), 
      ticket.getTicketType(),
      ticket.getPrice(), 
      ticket.getAvailableQuantity(), 
      (ticket.getTotalQuantity() - ticket.getAvailableQuantity()),
      ticket.getTicketDescription()
    );
  }

  public static List<TicketCard> mapToTicketCardList(List<Tickets> tickets) {
    return tickets.stream().map(DtoMapper::mapToTicketCard).toList();
  }
}
