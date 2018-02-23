package uk.ac.bbsrc.tgac.miso.service.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

  private final List<ValidationError> errors = new ArrayList<>();

  /**
   * Adds an error to the validation attempt. This also indicates validation failure
   * 
   * @param error describes the error
   */
  public void addError(ValidationError error) {
    errors.add(error);
  }

  /**
   * @return false if there are any errors; true otherwise
   */
  public boolean isValid() {
    return errors.isEmpty();
  }

  /**
   * throws a {@link ValidationException} if there were any errors; otherwise does nothing. If a ValidationException is thrown, it contains
   * all of the errors
   */
  public void throwIfInvalid() {
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

}