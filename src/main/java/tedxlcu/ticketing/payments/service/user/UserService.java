package tedxlcu.ticketing.payments.service.user;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tedxlcu.ticketing.payments.DTO.SendNewAccountDTO;
import tedxlcu.ticketing.payments.Exception.AlreadyExistsException;
import tedxlcu.ticketing.payments.Exception.ForbiddenException;
import tedxlcu.ticketing.payments.Exception.ResourceNotFoundException;
import tedxlcu.ticketing.payments.Request.JwtResponse;
import tedxlcu.ticketing.payments.Request.LoginRequest;
import tedxlcu.ticketing.payments.Request.createUserRequest;
import tedxlcu.ticketing.payments.Response.UserDto;
import tedxlcu.ticketing.payments.enums.UserRole;
import tedxlcu.ticketing.payments.jobs.payload.EmailJobPayload;
import tedxlcu.ticketing.payments.jobs.payload.EmailJobType;
import tedxlcu.ticketing.payments.model.User;
import tedxlcu.ticketing.payments.repository.UserRepository;
import tedxlcu.ticketing.payments.security.jwt.JwtService;
import tedxlcu.ticketing.payments.service.EmailService;

@Service
@Slf4j 
@RequiredArgsConstructor 
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RedisTemplate<String, Object> redisTemplate;

  private static final Logger log = LoggerFactory.getLogger(UserService.class);
  
  @Value("${admin.url}")
  private String adminUrl;
  
  private boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  private void adminExists() {
    boolean itExists = userRepository.findAll().stream()
      .anyMatch(user -> user.getRole() == UserRole.ADMIN);
    if(itExists) {
      throw new AlreadyExistsException("ADMIN exists already");
    }
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

  @Cacheable(value = "authUser", key = "#userId")
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

  public void onboardAdmin(createUserRequest request) throws Exception {
    adminExists();
    if (existsByEmail(request.getEmail())) {
      throw new AlreadyExistsException("User with this email already exists");
    }

    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());

    userRepository.save(user);
  }

  public void createUser(createUserRequest request) throws Exception {
    if (existsByEmail(request.getEmail())) {
      throw new AlreadyExistsException("User with this email already exists");
    }
    if(request.getRole().equals(UserRole.ADMIN)){
      throw new ForbiddenException("cannot create duplicate ADMIN");
    }
    String generatePassword = "TEDX2026?" + UUID.randomUUID().toString();
    String uniquePassword = generatePassword.substring(0, 16);
    
    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(uniquePassword));
    user.setRole(request.getRole());

    userRepository.save(user);

    SendNewAccountDTO newAccountDTO = new SendNewAccountDTO(
      user.getEmail(),
      user.getFirstName(),
      user.getLastName(),
      user.getEmail(),
      uniquePassword,
      adminUrl + "/login"
    );

    // Send email with credentials
    EmailJobPayload wrapper = new EmailJobPayload(0, EmailJobType.NEW_ADMIN_ACCOUNT, newAccountDTO);

    try {
      // LPUSH pushes onto the left end of the list
      redisTemplate.opsForList().leftPush("queue:email:notifications", wrapper);
      log.info("Queued admin account email job for: {}", user.getEmail());
    } catch (Exception e) {
      // If Redis goes down, log aggressively so operations can recover the payload
      log.error("CRITICAL: Failed to push email job to Redis for user: {}. Error: {}", user.getEmail(), e.getMessage(), e);
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
