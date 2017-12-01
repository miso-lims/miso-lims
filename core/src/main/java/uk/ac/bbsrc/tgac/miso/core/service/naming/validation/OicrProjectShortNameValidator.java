package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class OicrProjectShortNameValidator extends RegexValidator {

  private static final String regex = "^[A-Z0-9]{3,5}$";

  public OicrProjectShortNameValidator() {
    super(regex, false, false);
    setValidationMessage("Short name must be 3-5 characters and include only capital letters and numbers");
  }

  @Override
  protected String getFieldName() {
    return "Short name";
  }

  @Override
  protected boolean customRegexOptionEnabled() {
    return false;
  }

  @Override
  protected boolean nullabilityOptionEnabled() {
    return false;
  }

  @Override
  protected boolean enableDuplicatesOptionEnabled() {
    return false;
  }

}
