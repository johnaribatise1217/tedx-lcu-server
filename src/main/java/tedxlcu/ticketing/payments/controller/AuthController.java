package tedxlcu.ticketing.payments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tedxlcu.ticketing.payments.Request.JwtResponse;
import tedxlcu.ticketing.payments.Request.LoginRequest;
import tedxlcu.ticketing.payments.Request.UpdatePasswordRequest;
import tedxlcu.ticketing.payments.Request.createUserRequest;
import tedxlcu.ticketing.payments.Response.ApiResponse;
import tedxlcu.ticketing.payments.security.user.AdminUserDetails;
import tedxlcu.ticketing.payments.service.user.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private UserService userService;

  private String getCurrentUserId(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof AdminUserDetails) {
      return ((AdminUserDetails) principal).getId();
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
    }
  }

  private boolean isAdmin(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof AdminUserDetails) {
      return ((AdminUserDetails) principal).getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
    } else {
      throw new IllegalStateException("You are not authorized to perform this action");
    }
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
    JwtResponse jwtResponse = userService.authenticateUser(loginRequest);
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "Login successful", jwtResponse)
    );
  }

  @PostMapping("/create-admin")
  public ResponseEntity<ApiResponse> 
  createAdmin(@RequestBody createUserRequest createUserRequest, 
  Authentication authentication) 
  throws Exception {
    if(!isAdmin(authentication)) {
      return ResponseEntity.status(403).body(
        new ApiResponse(false, "403", "You are not authorized to perform this action", null)
      );
    }
    userService.createUser(createUserRequest);
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "Admin created successfully", null)
    );
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse> getAuthUser(Authentication authentication) {
    String userId = getCurrentUserId(authentication);
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "Fetched successfully", userService.getAuthUser(userId))
    );
  }

  @PutMapping("/me/update-password")
  public ResponseEntity<ApiResponse> updatePassword(Authentication authentication , @RequestBody UpdatePasswordRequest request) {
    String userId = getCurrentUserId(authentication);
    userService.updateUserPassword(userId, request.getNewPassword() , request.getOldPassword());
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "Password updated successfully", null)
    );
  }
}
