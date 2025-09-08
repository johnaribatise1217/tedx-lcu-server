package tedxlcu.ticketing.payments.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String role; // e.g., "ADMIN", "SUBADMIN"
}
