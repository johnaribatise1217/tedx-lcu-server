package tedxlcu.ticketing.payments.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DiscountExpiredException extends RuntimeException{
  public DiscountExpiredException(String message) {
   super(message);
  }
}
