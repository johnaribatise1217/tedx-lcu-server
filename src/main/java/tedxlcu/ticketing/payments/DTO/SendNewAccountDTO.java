package tedxlcu.ticketing.payments.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class SendNewAccountDTO {
  private String to;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String loginUrl;
}
