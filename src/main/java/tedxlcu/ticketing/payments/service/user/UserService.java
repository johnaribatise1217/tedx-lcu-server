package tedxlcu.ticketing.payments.service.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tedxlcu.ticketing.payments.Exception.AlreadyExistsException;
import tedxlcu.ticketing.payments.Request.createUserRequest;
import tedxlcu.ticketing.payments.model.User;
import tedxlcu.ticketing.payments.repository.UserRepository;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  private boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public void createUser(createUserRequest request) {
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
  }

  public void updateUserPassword(String email, String newPassword , String oldPassword) {
    User existingUser = userRepository.findByEmail(email);
    if (existingUser == null) {
      throw new AlreadyExistsException("User with this email does not exist");
    }
    if(!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
      throw new RuntimeException("Old password is incorrect");
    }
    existingUser.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(existingUser);
  }
}
