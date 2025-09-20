package tedxlcu.ticketing.payments.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tedxlcu.ticketing.payments.DTO.Social;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="speakers")
public class Speaker {
  private String id;
  private String bio;
  private String title;
  private String fullName;
  private String speakerImage;
  private List<Social> socialLinks;

  private String createdBy;
  private String updatedBy;
  
  @CreatedDate
  private LocalDateTime createdAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
