package tedxlcu.ticketing.payments.service.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tedxlcu.ticketing.payments.Exception.AlreadyExistsException;
import tedxlcu.ticketing.payments.Exception.ResourceNotFoundException;
import tedxlcu.ticketing.payments.Request.JwtResponse;
import tedxlcu.ticketing.payments.Request.LoginRequest;
import tedxlcu.ticketing.payments.Request.createUserRequest;
import tedxlcu.ticketing.payments.Response.UserDto;
import tedxlcu.ticketing.payments.model.User;
import tedxlcu.ticketing.payments.repository.UserRepository;
import tedxlcu.ticketing.payments.security.jwt.JwtService;
import tedxlcu.ticketing.payments.service.EmailService;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private EmailService emailService;
  @Value("${admin.url}")
  private String adminUrl;
  
  private boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public JwtResponse authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        loginRequest.getEmail(),
        loginRequest.getPassword()
      )
    );

    User user = Optional.ofNullable(userRepository.findByEmail(loginRequest.getEmail()))
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtService.generateToken(authentication);
    JwtResponse jwtResponse = new JwtResponse(jwt, user.getId());
    return jwtResponse;
  }

  public UserDto getAuthUser(String userId) {
    User user = userRepository.findById(userId).orElseThrow(
      () -> new ResourceNotFoundException("User not found")
    );
    return new UserDto(
      user.getId(),
      user.getFirstName(),
      user.getLastName(),
      user.getEmail(),
      user.getRole().name()
    );
  }

  public void createUser(createUserRequest request) throws Exception {
    if (existsByEmail(request.getEmail())) {
      throw new AlreadyExistsException("User with this email already exists");
    }
    String generatePassword = "TEDX2025?" + UUID.randomUUID().toString();
    String uniquePassword = generatePassword.substring(0, 16);
    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());

    if(request.getRole().name().equals("SUBADMIN")) {
      user.setPassword(passwordEncoder.encode(uniquePassword));
    } else {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    user.setRole(request.getRole());
    userRepository.save(user);

    // Send email with credentials
    if(request.getRole().name().equals("SUBADMIN")) {
      // Send email with credentials
      String loginUrl = adminUrl + "/login";
      emailService.sendNewAccountMail(user.getEmail(), user.getFirstName(), user.getLastName(), user.getEmail(), uniquePassword, loginUrl);
    }
  }

  public void updateUserPassword(String userId, String newPassword , String oldPassword) {
    User existingUser = userRepository.findById(userId).orElseThrow(
      () -> new AlreadyExistsException("User with this email does not exist")
    );
    if(!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
      throw new RuntimeException("Old password is incorrect");
    }
    existingUser.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(existingUser);
  }
}
