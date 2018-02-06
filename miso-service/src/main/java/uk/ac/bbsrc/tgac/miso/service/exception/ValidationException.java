package uk.ac.bbsrc.tgac.miso.service.exception;

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
  }

  /**
   * @return a list of the errors explaining why the action in question has failed validation
   */
  public List<ValidationError> getErrors() {
    return errors;
  }

  public Map<String, String> getErrorsByField() {
    return errors.stream()
        .collect(Collectors.toMap(ValidationError::getProperty, ValidationError::getMessage, (msg1, msg2) -> msg1 + "\n" + msg2));
  }

}
