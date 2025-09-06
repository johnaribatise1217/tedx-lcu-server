package tedxlcu.ticketing.payments.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tedxlcu.ticketing.payments.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
  User findByEmail(String email);
  public boolean existsByEmail(String email);
}
