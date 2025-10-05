package tedxlcu.ticketing.payments.Request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateDiscountWindow {
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private int percentage;
  private String discountName;
  private String dicountCode;
  private int usageLimit;
}
