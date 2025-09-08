package tedxlcu.ticketing.payments.security.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tedxlcu.ticketing.payments.security.user.AdminUserDetailsService;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {
  @Autowired
  private JwtService jwtService;
  @Autowired
  private HandlerExceptionResolver handlerExceptionResolver;
  @Autowired
  private AdminUserDetailsService adminUserDetailsService;

  @SuppressWarnings("null")
  @Override
  protected void doFilterInternal(
    HttpServletRequest request, 
    HttpServletResponse response, 
    FilterChain filterChain
  )
    throws ServletException, IOException 
  {
    try {
      String jwt = parseJwt(request);
      if (!StringUtils.hasText(jwt)) {
        filterChain.doFilter(request, response);
        return;
      }
      String userName = jwtService.extractUsername(jwt);

      if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if(jwtService.validateToken(jwt)) {
          UserDetails userDetails = adminUserDetailsService.loadUserByUsername(userName);
          UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
          );
          auth.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
          );
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }
    } catch (JwtException | IllegalArgumentException | NullPointerException e) {
      handlerExceptionResolver.resolveException(request, response, null, e);
    } 
    filterChain.doFilter(request, response);
  } 

  private String parseJwt(HttpServletRequest request){
    String headerAuth = request.getHeader("Authorization");
    if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
      return headerAuth.substring(7);
    }
    return null;
  }
}
