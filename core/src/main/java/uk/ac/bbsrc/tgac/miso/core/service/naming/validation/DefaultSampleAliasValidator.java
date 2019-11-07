package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;


public class DefaultSampleAliasValidator extends RegexValidator {

  private static final String regex = "([A-z0-9]+)_S([A-z0-9]+)_(.*)";

  public DefaultSampleAliasValidator() {
    super(regex, false, false);
  }

  @Override
  protected String getFieldName() {
    return "alias";
  }

  @Override
  protected boolean customRegexOptionEnabled() {
    return true;
  }

  @Override
  protected boolean nullabilityOptionEnabled() {
    return false;
  }

  @Override
  protected boolean enableDuplicatesOptionEnabled() {
    return true;
  }

}
