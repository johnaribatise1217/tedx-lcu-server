package tedxlcu.ticketing.payments.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tedxlcu.ticketing.payments.enums.UserRole;

@Data
@Document(collection = "users") 
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  
  private UserRole role; // e.g., "ADMIN", "SUBADMIN"
}
