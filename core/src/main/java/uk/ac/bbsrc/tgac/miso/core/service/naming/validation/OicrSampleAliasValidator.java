package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class OicrSampleAliasValidator extends RegexValidator {

  public static final String IDENTITY_REGEX_PART = "(" + OicrProjectShortNameValidator.REGEX + ")_(\\d{3,})";
  public static final String TISSUE_NAME_REGEX = "[A-Za-z0-9]+";
  public static final String TISSUE_REGEX_PART = TISSUE_NAME_REGEX + "_" + TISSUE_NAME_REGEX + "_(nn|\\d{2})_(\\d{1,2})-(\\d{1,2})";
  private static final String ANALYTE_REGEX_PART = "(C|CV|HE|SL|LCM|SC|D_S|R_S|D_|R_(\\d+_(MR|SM|WT)_)?)\\d+";

  private static final String REGEX = "^" + IDENTITY_REGEX_PART + "(_" + TISSUE_REGEX_PART + "(_" + ANALYTE_REGEX_PART + ")?)?$";

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
