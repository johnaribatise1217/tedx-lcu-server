package tedxlcu.ticketing.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
@EnableAsync
@EnableCaching 
public class PaymentsApplication {
	static{
		tedxlcu.ticketing.payments.EnvLoader loader = 
		new tedxlcu.ticketing.payments.EnvLoader();
	}

	public static void main(String[] args) {
		SpringApplication.run(PaymentsApplication.class, args);
	}

}
