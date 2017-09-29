package uk.ac.bbsrc.tgac.miso.core.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Signals that an I/O exception has occurred due to missing permissions
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationIOException extends IOException {

  private static final long serialVersionUID = 5648289043434754550L;

  public AuthorizationIOException() {
  }

  public AuthorizationIOException(String message) {
    super(message);
  }

  public AuthorizationIOException(Throwable cause) {
    super(cause);
  }

  public AuthorizationIOException(String message, Throwable cause) {
    super(message, cause);
  }

}
