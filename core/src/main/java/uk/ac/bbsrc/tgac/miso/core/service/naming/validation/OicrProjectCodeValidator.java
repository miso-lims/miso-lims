package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class OicrProjectCodeValidator extends RegexValidator {

  public static final String REGEX = "[A-Z0-9]{3,10}";

  public OicrProjectCodeValidator() {
    super("^" + REGEX + "$", false, false);

    setValidationMessage("Code must be 3-10 characters and include only capital letters and numbers");
  }

  @Override
  protected String getFieldName() {
    return "Code";
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
