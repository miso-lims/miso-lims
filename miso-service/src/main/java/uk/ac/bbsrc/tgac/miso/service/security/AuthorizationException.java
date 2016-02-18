package uk.ac.bbsrc.tgac.miso.service.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Runtime exception to be thrown when a user is unauthorized to perform the action they are attempting
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends RuntimeException {

  private static final long serialVersionUID = 9191268105381900949L;

  public AuthorizationException() {
    super();
  }

  public AuthorizationException(String message) {
    super(message);
    
  }

  public AuthorizationException(Throwable cause) {
    super(cause);
    
  }

  public AuthorizationException(String message, Throwable cause) {
    super(message, cause);
    
  }

  public AuthorizationException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    
  }

}
