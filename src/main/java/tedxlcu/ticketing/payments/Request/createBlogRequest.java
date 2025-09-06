package tedxlcu.ticketing.payments.Request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class createBlogRequest {
  private String title;
  private String subTitle;
  private int numParagraphs;
  private List<String> paragraphs;
  private String thumbnailUrl; // URL of the thumbnail image
  private String author;
  private String authorImageUrl; // URL of the author's image
  private String authorBio;
  private List<String> tags;
  private String readTime; // e.g., "5 min read"
  private List<String> blogImages; // URLs of images within the blog content

  private String createdBy; //the name of the admin who created the blog
  private String updatedBy; //the name of the admin who last updated the blog
}
