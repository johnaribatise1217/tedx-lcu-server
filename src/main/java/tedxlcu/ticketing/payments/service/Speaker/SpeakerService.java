package tedxlcu.ticketing.payments.service.Speaker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tedxlcu.ticketing.payments.Exception.ResourceNotFoundException;
import tedxlcu.ticketing.payments.Request.createSpeakerRequest;
import tedxlcu.ticketing.payments.model.Speaker;
import tedxlcu.ticketing.payments.model.User;
import tedxlcu.ticketing.payments.repository.SpeakerRepository;
import tedxlcu.ticketing.payments.repository.UserRepository;

@Service
public class SpeakerService implements ISpeakerService {

  @Autowired
  private SpeakerRepository speakerRepository;
  @Autowired
  private UserRepository userRepository;

  @Override
  public void createSpeaker(createSpeakerRequest req, String userId) {
    User user = userRepository.findById(userId).orElseThrow(
      () -> new ResourceNotFoundException("User not found")
    );
    Speaker newSpeaker = new Speaker();
    newSpeaker.setBio(req.getBio());
    newSpeaker.setFullName(req.getFullName());
    newSpeaker.setSpeakerImage(req.getSpeakerImage());
    newSpeaker.setTitle(req.getTitle());
    newSpeaker.setCreatedBy(user.getFirstName() + " " + user.getLastName());
    speakerRepository.save(newSpeaker);
  }

  @Override
  public Speaker getSpeakerById(String id) {
    return speakerRepository.findById(id).orElseThrow(
      () -> new ResourceNotFoundException("Speaker not found")
    );
  }

  @Override
  public List<Speaker> getAllSpeakers() {
    return speakerRepository.findAll();
  }

  @Override
  public void UpdateSpeaker(String id, createSpeakerRequest request, String userId) {
    User user = userRepository.findById(userId).orElseThrow(
      () -> new ResourceNotFoundException("User not found")
    );
    Speaker existingSpeaker = getSpeakerById(id);
    existingSpeaker.setBio(request.getBio());
    existingSpeaker.setFullName(request.getFullName());
    existingSpeaker.setTitle(request.getTitle());
    existingSpeaker.setSpeakerImage(request.getSpeakerImage());
    existingSpeaker.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
    
    speakerRepository.save(existingSpeaker);
  }

  @Override
  public void deleteSpeaker(String id) {
    speakerRepository.deleteById(id);
  }

}
