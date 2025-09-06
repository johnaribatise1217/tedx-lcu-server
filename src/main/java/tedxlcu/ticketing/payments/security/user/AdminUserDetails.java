package tedxlcu.ticketing.payments.security.user;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tedxlcu.ticketing.payments.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDetails implements UserDetails {
  private String id;
  private String email;
  private String password;
  private Collection<GrantedAuthority> authorities;

  public static AdminUserDetails buildAdminUserDetails(User user) {
    List<GrantedAuthority> authorities = Collections.singletonList(
      new SimpleGrantedAuthority(user.getRole().name())
    );
    return new AdminUserDetails(
      user.getId(),
      user.getEmail(),
      user.getPassword(),
      authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

}
