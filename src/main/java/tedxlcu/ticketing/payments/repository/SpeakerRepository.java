package tedxlcu.ticketing.payments.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tedxlcu.ticketing.payments.model.Speaker;

@Repository
public interface SpeakerRepository extends MongoRepository<Speaker, String> {
  Speaker findByFullName(String fullName);
}
