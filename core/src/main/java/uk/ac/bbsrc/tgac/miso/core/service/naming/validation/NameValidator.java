package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;


public interface NameValidator {

  /**
   * Optional
   * 
   * @param validationRegex
   */
  void setValidationRegex(String validationRegex);

  /**
   * Optional
   * 
   * @param message
   */
  void setValidationMessage(String message);

  /**
   * Optional
   * 
   * @param allow
   */
  void setDuplicateAllowed(boolean allow);

  /**
   * Optional
   * 
   * @param nullable
   */
  void setNullAllowed(boolean nullable);

  ValidationResult validate(String value);

  boolean duplicatesAllowed();

  boolean nullsAllowed();

}
