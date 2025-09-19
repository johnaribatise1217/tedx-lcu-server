package tedxlcu.ticketing.payments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tedxlcu.ticketing.payments.Request.createSpeakerRequest;
import tedxlcu.ticketing.payments.Response.ApiResponse;
import tedxlcu.ticketing.payments.security.user.AdminUserDetails;
import tedxlcu.ticketing.payments.service.Speaker.ISpeakerService;

@RestController
@RequestMapping("/api/speakers")
public class SpeakerController {

  @Autowired
  private ISpeakerService speakerService;

  private String getCurrentUserId(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof AdminUserDetails) {
      return ((AdminUserDetails) principal).getId();
    } else {
      throw new IllegalStateException("Unexpected principal type: ");
    }
  }

  @GetMapping("/get-all")
  public ResponseEntity<ApiResponse> getAllSpeakers(){
    return ResponseEntity.ok().body(
      new ApiResponse(
        true, "200", "fetched successfully", speakerService.getAllSpeakers())
    );
  }

  @GetMapping("/view/{id}")
  public ResponseEntity<ApiResponse> getSingleSpeaker(@PathVariable String id){
    return ResponseEntity.ok().body(
      new ApiResponse(
        true, "200", "fetched successfully", speakerService.getSpeakerById(id))
    );
  }

  @PostMapping("/create")
  public ResponseEntity<ApiResponse> createSpeaker(Authentication authentication,
    @RequestBody createSpeakerRequest request 
  ){
    String userId = getCurrentUserId(authentication);
    speakerService.createSpeaker(request, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(
      new ApiResponse(
        true, "200", "created successfully", null)
    );
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<ApiResponse> updateSpeaker(Authentication authentication,
    @PathVariable String id,
    @RequestBody createSpeakerRequest request
  ){
    String userId = getCurrentUserId(authentication);
    speakerService.UpdateSpeaker(id, request, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(
      new ApiResponse(
        true, "200", "updated successfully", null)
    );
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ApiResponse> deleteSpeaker(
    @PathVariable String id
  ){
    speakerService.deleteSpeaker(id);
    return ResponseEntity.status(HttpStatus.CREATED).body(
      new ApiResponse(
        true, "200", "deleted successfully", null)
    );
  }
}
