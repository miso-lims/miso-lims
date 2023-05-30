package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class OicrProjectCodeValidator extends RegexValidator {

  public static final String REGEX = "[A-Z0-9]{3,10}";

  public OicrProjectCodeValidator() {
    super("^" + REGEX + "$", false, false);

    setValidationMessage("Short name must be 3-10 characters and include only capital letters and numbers");
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
