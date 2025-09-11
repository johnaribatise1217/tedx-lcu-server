package tedxlcu.ticketing.payments.Exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import tedxlcu.ticketing.payments.Response.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(AlreadyExistsException.class)
  public ResponseEntity<ErrorResponse>
  handleAlreadyExistsException(
    AlreadyExistsException ex, WebRequest request
  ) {
    ErrorResponse error = new ErrorResponse(
      false,
      request.getDescription(false),
      HttpStatus.CONFLICT,
      ex.getMessage(),
      LocalDateTime.now()
    );
    return new ResponseEntity<>(error, error.getErrorCode());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse>
  handleResourceNotFoundException(
    ResourceNotFoundException ex, WebRequest request
  ) {
    ErrorResponse error = new ErrorResponse(
      false,
      request.getDescription(false),
      HttpStatus.NOT_FOUND,
      ex.getMessage(),
      LocalDateTime.now()
    );
    return new ResponseEntity<>(error, error.getErrorCode());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse>
  handleGlobalException(
    Exception ex, WebRequest request
  ) {
    ErrorResponse error = new ErrorResponse(
      false,
      request.getDescription(false),
      HttpStatus.INTERNAL_SERVER_ERROR,
      ex.getMessage(),
      LocalDateTime.now()
    );
    return new ResponseEntity<>(error, error.getErrorCode());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse>
  handleBadCredentialsException(
    BadCredentialsException ex, WebRequest request
  ) {
    ErrorResponse error = new ErrorResponse(
      false,
      request.getDescription(false),
      HttpStatus.UNAUTHORIZED,
      ex.getMessage(),
      LocalDateTime.now()
    );
    return new ResponseEntity<>(error, error.getErrorCode());
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse>
  handleUsernameNotFoundException(
    UsernameNotFoundException exception, WebRequest request
  ){
    ErrorResponse error = new ErrorResponse(
      false,
      request.getDescription(false),
      HttpStatus.NOT_FOUND,
      exception.getMessage(),
      LocalDateTime.now()
    ); 
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorResponse>
  handleExpiredJwtException(
    ExpiredJwtException exception, WebRequest request
  ){
    ErrorResponse error = new ErrorResponse(
      false,
      request.getDescription(false),
      HttpStatus.UNAUTHORIZED,
      "Your session has expired, please login again",
      LocalDateTime.now()
    );
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }  

  @ExceptionHandler(MalformedJwtException.class)
  public ResponseEntity<ErrorResponse>
  handleMalformedJwtException(
    MalformedJwtException exception, WebRequest request
  ){
    ErrorResponse error = new ErrorResponse(
      false,
      request.getDescription(false),
      HttpStatus.BAD_REQUEST,
      "Invalid token",
      LocalDateTime.now()
    );
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }
}
