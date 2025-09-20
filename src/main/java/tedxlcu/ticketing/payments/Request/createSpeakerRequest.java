package tedxlcu.ticketing.payments.Request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import tedxlcu.ticketing.payments.DTO.Social;

@Data
@AllArgsConstructor
public class createSpeakerRequest {
  private String bio;
  private String title;
  private String fullName;
  private String speakerImage;
  private List<Social> socialLinks;
}
