package tedxlcu.ticketing.payments.logger;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class GlobalRequestLoggingFilter extends OncePerRequestFilter{

  private static final Logger logger = LoggerFactory.getLogger(GlobalRequestLoggingFilter.class);
  
  @Override
  protected void doFilterInternal(
    HttpServletRequest request, 
    HttpServletResponse response, 
    FilterChain filterChain
  )
      throws ServletException, IOException {
    long startTime = System.currentTimeMillis();

    //Log incoming request details
    logger.info(">>> INCOMING REQUEST: {} {} from IP: {}", 
            request.getMethod(), 
            request.getRequestURI(), 
            request.getRemoteAddr());

    try {
      // Let the request continue to the Controller
      filterChain.doFilter(request, response);
    } finally {
        // Log outgoing response details after execution
        long duration = System.currentTimeMillis() - startTime;
        logger.info("<<< OUTGOING RESPONSE: {} {} | Status: {} | Taken: {}ms", 
                request.getMethod(), 
                request.getRequestURI(), 
                response.getStatus(), 
                duration);
    }
  }

}
