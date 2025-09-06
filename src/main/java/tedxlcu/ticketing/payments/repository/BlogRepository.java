package tedxlcu.ticketing.payments.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tedxlcu.ticketing.payments.model.Blog;

@Repository
public interface BlogRepository extends MongoRepository<Blog, String> {

}
