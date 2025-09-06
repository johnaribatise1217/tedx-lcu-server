package tedxlcu.ticketing.payments.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import tedxlcu.ticketing.payments.enums.UserRole;

@Data
@AllArgsConstructor
public class createUserRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private UserRole role; // e.g., "ADMIN", "SUBADMIN"
}
