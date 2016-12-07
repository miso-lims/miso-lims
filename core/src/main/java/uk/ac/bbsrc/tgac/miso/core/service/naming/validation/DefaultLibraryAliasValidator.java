package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class DefaultLibraryAliasValidator extends RegexValidator {

  private static final String regex = "([A-z0-9]+)_L([A-z0-9]+)-([A-Z0-9]+)_(.*)";

  public DefaultLibraryAliasValidator() {
    super(regex, false, false);
  }

  @Override
  protected String getFieldName() {
    return "alias";
  }

  @Override
  protected boolean nullabilityOptionEnabled() {
    return false;
  }

  @Override
  protected boolean enableDuplicatesOptionEnabled() {
    return true;
  }

  @Override
  protected boolean customRegexOptionEnabled() {
    return true;
  }

}
