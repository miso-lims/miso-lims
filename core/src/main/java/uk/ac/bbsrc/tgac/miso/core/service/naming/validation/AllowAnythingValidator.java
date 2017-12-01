package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;


public class AllowAnythingValidator implements NameValidator {

  @Override
  public void setValidationRegex(String validationRegex) {
    throw new UnsupportedOperationException("Customization not supported by this validator");
  }

  @Override
  public void setValidationMessage(String message) {
    throw new UnsupportedOperationException("Customization not supported by this validator");
  }

  @Override
  public void setDuplicateAllowed(boolean allow) {
    throw new UnsupportedOperationException("Customization not supported by this validator");
  }

  @Override
  public void setNullAllowed(boolean nullable) {
    throw new UnsupportedOperationException("Customization not supported by this validator");
  }

  @Override
  public ValidationResult validate(String value) {
    return ValidationResult.success();
  }

  @Override
  public boolean duplicatesAllowed() {
    return true;
  }

  @Override
  public boolean nullsAllowed() {
    return true;
  }

}
