package tedxlcu.ticketing.payments.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tedxlcu.ticketing.payments.model.DiscountWindow;

@Repository
public interface DiscountRepository extends MongoRepository<DiscountWindow, String>{
  Optional<DiscountWindow> findByCode(String code);
}
