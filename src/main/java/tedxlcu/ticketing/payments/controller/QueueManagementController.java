package tedxlcu.ticketing.payments.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController 
@RequestMapping("/api/admin/queue")
@RequiredArgsConstructor
@Slf4j
public class QueueManagementController {

  private final RedisTemplate<String, Object> redisTemplate;
  private static final String QUEUE_PRIMARY = "queue:email:notifications";
  private static final String QUEUE_DLQ = "queue:email:notifications:dlq";

  @GetMapping("/status")
  public ResponseEntity<Map<String, Long>> getQueueStatus() {
    Long primarySize = redisTemplate.opsForList().size(QUEUE_PRIMARY);
    Long dlqSize = redisTemplate.opsForList().size(QUEUE_DLQ);

    Map<String, Long> status = new HashMap<>();
    status.put("primaryQueueSize", primarySize != null ? primarySize : 0L);
    status.put("deadLetterQueueSize", dlqSize != null ? dlqSize : 0L);

    return ResponseEntity.ok(status);
  }

  @PostMapping("/replay")
  public ResponseEntity<Map<String, Object>> replayDlq() {
    Long dlqSize = redisTemplate.opsForList().size(QUEUE_DLQ);
    Map<String, Object> response = new HashMap<>();

    if (dlqSize == null || dlqSize == 0) {
      response.put("message", "No jobs found in the Dead Letter Queue to replay.");
      response.put("replayedCount", 0);
      return ResponseEntity.ok(response);
    }

    long replayedCount = 0;
    log.info("Starting replay of {} jobs from DLQ to primary queue...", dlqSize);

    // Atomically pop from DLQ and push to primary queue until DLQ is empty
    while (true) {
      try {
        // rightPopAndLeftPush moves the item safely from DLQ back to primary
        Object job = redisTemplate.opsForList().rightPopAndLeftPush(QUEUE_DLQ, QUEUE_PRIMARY);
        
        if (job == null) {
          break; // DLQ is completely empty
        }
        
        replayedCount++;
      } catch (Exception e) {
        log.error("Error occurred while replaying a job from DLQ: {}", e.getMessage());
        response.put("error", "Interrupted halfway through execution due to Redis connection drop.");
        break;
      }
    }

    log.info("Successfully replayed {} jobs back into the active loop.", replayedCount);
    response.put("message", "Replay execution completed successfully.");
    response.put("replayedCount", replayedCount);
    
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/purge")
  public ResponseEntity<Map<String, String>> purgeDlq() {
    redisTemplate.delete(QUEUE_DLQ);
    log.warn("Dead Letter Queue: {} was manually purged by an admin.", QUEUE_DLQ);
    
    Map<String, String> response = new HashMap<>();
    response.put("message", "Dead letter queue cleared successfully.");
    return ResponseEntity.ok(response);
  }
}
