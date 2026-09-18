package tedxlcu.ticketing.payments.jobs;

import java.time.Duration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tedxlcu.ticketing.payments.DTO.SendNewAccountDTO;
import tedxlcu.ticketing.payments.jobs.payload.EmailJobPayload;
import tedxlcu.ticketing.payments.model.TicketBooking;
import tedxlcu.ticketing.payments.service.EmailService;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailQueueWorker implements CommandLineRunner {
  private final RedisTemplate<String, Object> redisTemplate;
  private final EmailService emailService;
  private final ObjectMapper objectMapper; // Spring automatically provides this

  private static final String QUEUE_PRIMARY = "queue:email:notifications";
  private static final String QUEUE_DLQ = "queue:email:notifications:dlq";
  private static final int MAX_RETRIES = 3;

  public void run(String... args) {
    new Thread(this::listenToQueue, "email-worker-thread").start();
    log.info("Redis Multi-Type Email Queue Worker running.");
  }

  private void listenToQueue() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        // Blocks up to 5 seconds waiting for incoming jobs
        EmailJobPayload job = (EmailJobPayload) redisTemplate.opsForList().rightPop(QUEUE_PRIMARY, Duration.ofSeconds(5));

        if (job != null) {
          processJob(job);
        }
      } catch (Exception e) {
        log.error("Polling error from Redis queue: {}", e.getMessage());
        try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
      }
    }
  }

  private void processJob(EmailJobPayload job) {
    if (job.getEmailType() == null) {
      log.error("Discarding job with null EmailType: {}", job);
      return;
    }

    log.info("Processing [{}] job. Attempt: {}", job.getEmailType(), job.getRetryCount() + 1);

    try {
      switch (job.getEmailType()) {
        case NEW_ADMIN_ACCOUNT -> {
          // Safe conversion from JSON/LinkedHashMap to exact DTO class
          SendNewAccountDTO accountDto = objectMapper.convertValue(job.getPayload(), SendNewAccountDTO.class);
          emailService.sendNewAccountMail(
            accountDto.getTo(), accountDto.getFirstName(), accountDto.getLastName(),
            accountDto.getEmail(), accountDto.getPassword(), accountDto.getLoginUrl()
          );
          log.info("Admin account email delivered successfully to {}", accountDto.getTo());
        }
        case TICKET_CONFIRMATION -> {
          // Safe conversion from JSON/LinkedHashMap to exact TicketBooking entity class
          TicketBooking booking = objectMapper.convertValue(job.getPayload(), TicketBooking.class);
          emailService.sendTicketMail(booking);
          log.info("Ticket confirmation email delivered successfully to {}", booking.getEmail());
        }
      }
    } catch (Exception ex) {
      log.error("Execution failed for job type [{}]. Error: {}", job.getEmailType(), ex.getMessage(), ex);
      handleFailure(job);
    }
  }

  private void handleFailure(EmailJobPayload job) {
    job.incrementRetryCount();
    
    if (job.getRetryCount() < MAX_RETRIES) {
      log.warn("Re-queueing task [{}]. Preparing for retry #{}", job.getEmailType(), job.getRetryCount());
      try {
        redisTemplate.opsForList().leftPush(QUEUE_PRIMARY, job);
      } catch (Exception re) {
        log.error("Failed back-push to Redis for retry: {}", re.getMessage());
      }
    } else {
      log.error("CRITICAL: Job [{}] exhausted after {} retries. Forwarding to DLQ.", job.getEmailType(), MAX_RETRIES);
      try {
        redisTemplate.opsForList().leftPush(QUEUE_DLQ, job);
      } catch (Exception dlqEx) {
        log.error("SYSTEM FATAL: Could not persist failed payload to DLQ! Payload: {}", job, dlqEx);
      }
    }
  }
}
