package tedxlcu.ticketing.payments.service.Speaker;

import java.util.List;

import tedxlcu.ticketing.payments.Request.createSpeakerRequest;
import tedxlcu.ticketing.payments.model.Speaker;

public interface ISpeakerService {
  void createSpeaker(createSpeakerRequest req, String userId);
  Speaker getSpeakerById(String id);
  List<Speaker> getAllSpeakers();
  void UpdateSpeaker(String id, createSpeakerRequest request, String userId);
  void deleteSpeaker(String id);
}
