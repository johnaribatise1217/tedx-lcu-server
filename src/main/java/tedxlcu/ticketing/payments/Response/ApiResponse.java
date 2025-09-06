package tedxlcu.ticketing.payments.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
  private boolean success;
  private String statusCode;
  private String statusMessage;
  private Object data;
}