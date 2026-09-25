package tedxlcu.ticketing.payments.Response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class ValidCodeResponse {
  private boolean isValid;
  private int percentage; 
  private LocalDateTime endDate;
  private String discountCode;
}
