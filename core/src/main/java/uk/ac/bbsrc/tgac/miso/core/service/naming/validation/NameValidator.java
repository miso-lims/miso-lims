package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;


/**
 * Interface for validating one of an entity's identifying String fields. This is not always a "name" field
 */
public interface NameValidator {

  /**
   * Optional method. Sets a custom regex to validate against
   * 
   * @param validationRegex
   * @throws UnsupportedOperationException if this NameValidator does not support custom regex
   */
  void setValidationRegex(String validationRegex);

  /**
   * Optional method. Sets a custom validation failed message
   * 
   * @param message
   * @throws UnsupportedOperationException if this NameValidator does not support custom validation messages
   */
  void setValidationMessage(String message);

  /**
   * Optional method. Changes whether duplicates are allowed in the field to be validated
   * 
   * @param allow
   * @throws UnsupportedOperationException if this NameValidator does not support this customisation
   */
  void setDuplicateAllowed(boolean allow);

  /**
   * Optional method. Changes whether null values are allowed in the field to be validated
   * 
   * @param nullable
   * @throws UnsupportedOperationException if this NameValidator does not support this customisation
   */
  void setNullAllowed(boolean nullable);

  ValidationResult validate(String value);

  boolean duplicatesAllowed();

  boolean nullsAllowed();

}
