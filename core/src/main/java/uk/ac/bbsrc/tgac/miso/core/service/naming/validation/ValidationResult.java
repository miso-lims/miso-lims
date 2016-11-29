package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class ValidationResult {

  private static ValidationResult SUCCESS = new ValidationResult(true, "validation was successful");

  private final boolean isValid;
  private final String message;

  private ValidationResult(boolean isValid, String message) {
    this.isValid = isValid;
    this.message = message;
  }

  public static ValidationResult success() {
    return SUCCESS;
  }

  public static ValidationResult failed(String message) {
    return new ValidationResult(false, message);
  }

  public boolean isValid() {
    return isValid;
  }

  public String getMessage() {
    return message;
  }

}
