package tedxlcu.ticketing.payments.service.Tickets;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tedxlcu.ticketing.payments.Exception.AlreadyExistsException;
import tedxlcu.ticketing.payments.Request.createTicketBookingReq;
import tedxlcu.ticketing.payments.Request.createTicketsReq;
import tedxlcu.ticketing.payments.model.TicketBooking;
import tedxlcu.ticketing.payments.model.Tickets;
import tedxlcu.ticketing.payments.repository.TicketBookingRepository;
import tedxlcu.ticketing.payments.repository.TicketRepository;

@Service
public class TicketsService implements ITicketsService{
  @Autowired
  private TicketRepository ticketRepository;
  @Autowired
  private TicketBookingRepository bookingRepository;

  @Override
  public void CreateTicket(createTicketsReq request) {
    Tickets newTickets = new Tickets();
    newTickets.setBenefits(request.getBenefits());
    newTickets.setName(request.getTicketName());
    newTickets.setPrice(request.getPrice());
    newTickets.setTicketType(request.getTicketType());
    newTickets.setTotalQuantity(request.getTotalQuantity());
    newTickets.setAvailableQuantity(request.getTotalQuantity());
    newTickets.setTicketDescription(request.getTicketDescription());
    ticketRepository.save(newTickets);
  }

  @Override
  public List<Tickets> GetAllTickets() {
    return ticketRepository.findAll();
  }

  @Override
  public TicketBooking creatTicketBooking(createTicketBookingReq request, String trxRef, String ticketId) {
    Optional.ofNullable(
      bookingRepository.findByTransactionReference(trxRef)
    ).ifPresent((error) -> {
      throw new AlreadyExistsException("Why are you trying to be fraudulent?");
    });

    TicketBooking newBooking = new TicketBooking();
    newBooking.setTransactionReference(trxRef);
    newBooking.setEmail(request.getEmail());
    newBooking.setPhone(request.getPhone());
    newBooking.setFirstName(request.getFirstName());
    newBooking.setLastName(request.getLastName());
    newBooking.setGender(request.getGender());
    newBooking.setInstitution(request.getInstitution());
    newBooking.setPaid(true);
    newBooking.setVerified(false);
    newBooking.setTicketTypeId(ticketId);
    newBooking.setAmountPaid(request.getAmount());
    newBooking.setTicketQuantity(request.getQuantity());

    return bookingRepository.save(newBooking);
  }
  
}
