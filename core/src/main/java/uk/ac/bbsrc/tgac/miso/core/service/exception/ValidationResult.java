package uk.ac.bbsrc.tgac.miso.core.service.exception;

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

  /**
   * Merges all of other's errors into this ValidationResult. If merging results from multiple entities, be sure that the entity is
   * identified in the message of each ValidationError
   * 
   * @param other the other ValidationResult to merge into this one
   */
  public void merge(ValidationResult other) {
    errors.addAll(other.errors);
  }

}