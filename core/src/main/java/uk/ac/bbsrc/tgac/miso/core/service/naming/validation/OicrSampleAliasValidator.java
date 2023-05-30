package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class OicrSampleAliasValidator extends RegexValidator {

  public static final String IDENTITY_REGEX_PART = "(" + OicrProjectCodeValidator.REGEX + ")_(\\d{3,})";
  public static final String TISSUE_NAME_REGEX = "[A-Za-z0-9]+";
  public static final String TISSUE_REGEX_PART =
      TISSUE_NAME_REGEX + "_" + TISSUE_NAME_REGEX + "_(nn|\\d{2})_(\\d+)-(\\d+)";
  private static final String ANALYTE_REGEX_PART = "\\w{1,5}(\\d+\\w{1,5})?\\d+";

  private static final String REGEX =
      "^" + IDENTITY_REGEX_PART + "(_" + TISSUE_REGEX_PART + "(_" + ANALYTE_REGEX_PART + ")?)?$";

  public OicrSampleAliasValidator() {
    super(REGEX, false, false);
  }

  @Override
  protected String getFieldName() {
    return "alias";
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
