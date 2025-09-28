package tedxlcu.ticketing.payments.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "discount_windows")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountWindow {
  @Id
  private String id;
  private String code; // unique 6-char code
  private int percentage; // e.g., 10 for 10%
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String discountName;

  public boolean isWindowOpen(LocalDateTime date){
    // LocalDateTime check = date == null ? LocalDateTime.now() : date;

    // // treat null start/end as unbounded (start==null => always started, end==null => no end)
    // boolean afterStart = (this.startDate == null) || !check.isBefore(this.startDate);
    // boolean beforeEnd  = (this.endDate == null)   || !check.isAfter(this.endDate);

     boolean inDateRange = startDate != null && endDate != null
        && (date.isEqual(startDate) || date.isAfter(startDate))
        && (date.isEqual(endDate) || date.isBefore(endDate));

      return inDateRange;

    // return afterStart && beforeEnd;
  }

  @CreatedDate
  private LocalDateTime createdAt;

  private String createdBy;
  private String updatedBy;
}
