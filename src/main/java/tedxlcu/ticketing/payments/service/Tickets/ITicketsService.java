package tedxlcu.ticketing.payments.service.Tickets;

import java.util.List;

import tedxlcu.ticketing.payments.DTO.TicketAdminDetails;
import tedxlcu.ticketing.payments.Request.createTicketBookingReq;
import tedxlcu.ticketing.payments.Request.createTicketsReq;
import tedxlcu.ticketing.payments.model.TicketBooking;
import tedxlcu.ticketing.payments.model.Tickets;

public interface ITicketsService {
  void CreateTicket(createTicketsReq request);
  TicketBooking getTicketById(String ticketId);
  List<Tickets> GetAllTickets();
  TicketBooking creatTicketBooking(createTicketBookingReq request, String trxRef, String ticketId);
  TicketAdminDetails getAllBookingsForAdmin();
  boolean verifyTicketBooking(String ticketId, String userId);
}
