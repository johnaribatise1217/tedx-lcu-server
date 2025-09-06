package tedxlcu.ticketing.payments.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.ResponseBody;

import tedxlcu.ticketing.payments.model.Tickets;

@ResponseBody
public interface TicketRepository extends MongoRepository<Tickets, String>{
  
}