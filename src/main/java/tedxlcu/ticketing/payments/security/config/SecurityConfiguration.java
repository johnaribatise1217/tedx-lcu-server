package tedxlcu.ticketing.payments.security.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import tedxlcu.ticketing.payments.security.jwt.AuthenticationTokenFilter;
import tedxlcu.ticketing.payments.security.user.AdminUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
  @Autowired
  private AdminUserDetailsService adminUserDetailsService;
  @Autowired
  private JwtEntryPoint authenticationEntryPoint;
  @Autowired
  private AuthenticationTokenFilter authenticationTokenFilter;

  private static final List<String> SECURED_URLS = List.of(
    "/api/blogs/create", "/api/blogs/{id}", "/api/blogs/update/{id}", "/api/blogs/delete/{id}" , "/api/auth/me/**" , "/api/auth/create-admin"
  );

  @Bean
  PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService userDetailsService(){
    return adminUserDetailsService;
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config)
          throws Exception
  {
    return config.getAuthenticationManager();
  }

  @Bean
  AuthenticationProvider authenticationProvider(){
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService());
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    
    return authenticationProvider;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) 
  throws Exception{
    http
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .csrf(AbstractHttpConfigurer :: disable)
      .authorizeHttpRequests(
        req -> req
          .requestMatchers(SECURED_URLS.toArray(new String[0])).authenticated()
          .anyRequest().permitAll()
      )
      .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
      http.authenticationProvider(authenticationProvider());
      http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
