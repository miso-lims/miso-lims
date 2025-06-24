package uk.ac.bbsrc.tgac.miso.core.service.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final List<ValidationError> errors;

  /**
   * Constructs a ValidationException to provide one or more errors explaining why validation failed
   * 
   * @param errors all of the errors related to the action in question
   */
  public ValidationException(List<ValidationError> errors) {
    super("Validation failed");
    this.errors = errors;
    for (ValidationError err : errors) { // TEMPORARY FOR TESTING
      throw new RuntimeException(err.getMessage(), getCause());
    }
  }

  /**
   * Constructs a ValidationException containing a single error explaining why validation failed
   * 
   * @param error the error related to the action in question
   */
  public ValidationException(ValidationError error) {
    super("Validation failed");
    this.errors = new ArrayList<>();
    this.errors.add(error);
  }

  /**
   * Constructs a ValidationException containing a single general validation error
   * 
   * @param message the general error message
   */
  public ValidationException(String message) {
    super("Validation failed");
    this.errors = new ArrayList<>();
    this.errors.add(new ValidationError(message));
  }

  /**
   * @return a list of the errors explaining why the action in question has failed validation
   */
  public List<ValidationError> getErrors() {
    return errors;
  }

  public Map<String, String> getErrorsByField() {
    return errors.stream()
        .collect(Collectors.toMap(ValidationError::getProperty, ValidationError::getMessage,
            (msg1, msg2) -> msg1 + "\n" + msg2));
  }

}
