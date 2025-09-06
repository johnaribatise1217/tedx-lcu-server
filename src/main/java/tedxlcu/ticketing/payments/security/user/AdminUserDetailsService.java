package tedxlcu.ticketing.payments.security.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tedxlcu.ticketing.payments.model.User;
import tedxlcu.ticketing.payments.repository.UserRepository;

@Service
public class AdminUserDetailsService implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;
  
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = Optional.ofNullable
      (
      userRepository.findByEmail(username)
      )
      .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    return AdminUserDetails.buildAdminUserDetails(user);
  }
}
