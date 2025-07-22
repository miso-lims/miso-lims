package uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate;

/**
 * Exception class to encapsulate errors reported by the scanner
 */
public class ScannerException extends Exception {

  private static final long serialVersionUID = -3551351083340873234L;

  /**
   * Creates a ScannerException with the specified detail message, or "Unknown scanner error" if the
   * specified message is null or an empty String
   * 
   * @param message exception detail message
   */
  public ScannerException(String message) {
    super(message == null || message.isEmpty() ? "Unknown scanner error" : message);
  }

}
