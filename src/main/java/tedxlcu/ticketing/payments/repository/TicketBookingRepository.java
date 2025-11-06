package tedxlcu.ticketing.payments.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tedxlcu.ticketing.payments.model.TicketBooking;

@Repository
public interface TicketBookingRepository extends MongoRepository<TicketBooking, String> {
  TicketBooking findByTransactionReference(String transactionReferece);
  TicketBooking findByTransactionReferenceAndEmail(String transactionReference, String email);
}
