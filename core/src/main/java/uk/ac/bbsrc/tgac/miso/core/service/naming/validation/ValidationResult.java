package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class ValidationResult {

  private static ValidationResult SUCCESS = new ValidationResult(true, "validation was successful");

  private final boolean isValid;
  private final String message;

  private ValidationResult(boolean isValid, String message) {
    this.isValid = isValid;
    this.message = message;
  }

  /**
   * @return a ValidationResult representing successful validation
   */
  public static ValidationResult success() {
    return SUCCESS;
  }

  /**
   * Creates a ValidationResult representing failed validation
   * 
   * @param message a detail message for the validation failure
   * @return
   */
  public static ValidationResult failed(String message) {
    return new ValidationResult(false, message);
  }

  /**
   * @return true if validation was successful; false otherwise
   */
  public boolean isValid() {
    return isValid;
  }

  /**
   * @return a user-friendly message detailing the validation results - useful as an error message if validation has failed
   */
  public String getMessage() {
    return message;
  }

}
