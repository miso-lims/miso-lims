package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

public class ServerErrorException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ServerErrorException(String message) {
    super(message);
  }

  public ServerErrorException(String message, Throwable cause) {
    super(message, cause);
  }

}
