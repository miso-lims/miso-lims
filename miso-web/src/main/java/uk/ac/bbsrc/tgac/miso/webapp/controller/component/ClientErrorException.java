package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

public class ClientErrorException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ClientErrorException(String message) {
    super(message);
  }

  public ClientErrorException(String message, Throwable cause) {
    super(message, cause);
  }

}
