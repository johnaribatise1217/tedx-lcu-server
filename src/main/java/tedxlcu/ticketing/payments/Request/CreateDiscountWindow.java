package tedxlcu.ticketing.payments.Request;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateDiscountWindow {
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private int percentage;
  private String discountName;
  private String discountCode;
  private int usageLimit;
}
