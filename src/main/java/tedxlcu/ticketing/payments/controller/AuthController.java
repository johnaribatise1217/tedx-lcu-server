package tedxlcu.ticketing.payments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tedxlcu.ticketing.payments.Exception.ForbiddenException;
import tedxlcu.ticketing.payments.Request.JwtResponse;
import tedxlcu.ticketing.payments.Request.LoginRequest;
import tedxlcu.ticketing.payments.Request.UpdatePasswordRequest;
import tedxlcu.ticketing.payments.Request.createUserRequest;
import tedxlcu.ticketing.payments.Response.ApiResponse;
import tedxlcu.ticketing.payments.Response.UserDto;
import tedxlcu.ticketing.payments.security.user.AdminUserDetails;
import tedxlcu.ticketing.payments.service.user.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor 
public class AuthController {
  private final UserService userService;

  private String getCurrentUserId(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof AdminUserDetails) {
      return ((AdminUserDetails) principal).getId();
    } else {
      throw new IllegalStateException("Unexpected principal type: ");
    }
  }

  private boolean isAdmin(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (!(principal instanceof AdminUserDetails)) {
      throw new ForbiddenException("You are not authorized to perform this action");
    }
    AdminUserDetails user = (AdminUserDetails) principal;

    // debug: list authorities (remove or reduce logging in production)
    var authNames = user.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .toList();

    // strict match — adjust to the exact granted authority your app uses ("ROLE_ADMIN" or "ADMIN")
    boolean ok = authNames.stream().anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ADMIN"));

    if (!ok) {
      throw new ForbiddenException("You are not authorized to perform this action. Your role: " + authNames);
    }
    return true;
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
    isAdmin(authentication);
    userService.createUser(createUserRequest);
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "subadmin created successfully", null)
    );
  }

  @PostMapping("/onboard-admin")
  public ResponseEntity<ApiResponse> 
  onboardAdmin(@RequestBody createUserRequest createUserRequest) 
  throws Exception {
    userService.onboardAdmin(createUserRequest);
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "Admin onboard successfully", null)
    );
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse> getAuthUser(Authentication authentication) {
    String userId = getCurrentUserId(authentication);
    UserDto userDto = userService.getAuthUser(userId);
    return ResponseEntity.ok(
      new ApiResponse(true, "200", "Fetched successfully", userDto)
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
