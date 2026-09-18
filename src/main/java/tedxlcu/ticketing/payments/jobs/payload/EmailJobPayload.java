package tedxlcu.ticketing.payments.jobs.payload;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class EmailJobPayload implements Serializable {
  private int retryCount = 0;
  private EmailJobType emailType;
  private Object payload;

  public void incrementRetryCount() {
    this.retryCount++;
  }
}
