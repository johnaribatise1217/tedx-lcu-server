package tedxlcu.ticketing.payments.Request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
  private String oldPassword;
  private String newPassword;
}
