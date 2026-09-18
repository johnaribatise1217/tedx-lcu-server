package tedxlcu.ticketing.payments.jobs;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service 
public class QueueProducer {
  private final RedisTemplate<String, Object> redisTemplate;
  private static final String QUEUE_KEY = "email_queue";

  public QueueProducer(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void sendTask(TaskMessage task) {
    redisTemplate.opsForList().leftPush(QUEUE_KEY, task);
  }
}
