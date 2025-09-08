package tedxlcu.ticketing.payments.Request;

import lombok.Data;

@Data
public class LoginRequest {
  private String email;
  private String password;
}
