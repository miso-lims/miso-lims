package uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate.mockServer;

/**
 * Exception class to represent errors that the VisionMate scanner simply ignores and does not send
 * any response to. These errors occur while processing requests, and likely indicate an illegal
 * argument or state
 */
public class InternalServerException extends Exception {

  private static final long serialVersionUID = -2448741922717821029L;

  public InternalServerException() {

  }

  public InternalServerException(String message) {
    super(message);
  }

  public InternalServerException(Throwable cause) {
    super(cause);
  }

  public InternalServerException(String message, Throwable cause) {
    super(message, cause);
  }

  public InternalServerException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
