package tedxlcu.ticketing.payments.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class createSpeakerRequest {
  private String bio;
  private String title;
  private String fullName;
  private String speakerImage;
}
