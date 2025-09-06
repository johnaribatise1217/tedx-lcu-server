package tedxlcu.ticketing.payments.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "blogs")
@AllArgsConstructor
@NoArgsConstructor
public class Blog {
  @Id
  private String id;
  private String title;
  private String subTitle;
  private int numParagraphs;
  private List<String> paragraphs;
  private String thumbnailUrl;
  private String author;
  private String authorImageUrl;
  private String authorBio;
  private List<String> tags;
  private String readTime; // e.g., "5 min read"
  private List<String> blogImages;

  private String createdBy; //the name of the admin who created the blog
  private String updatedBy; //the name of the admin who last updated the blog

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;
}
