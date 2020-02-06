package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class DefaultLibraryAliquotAliasValidator extends RegexValidator {

  public DefaultLibraryAliquotAliasValidator() {
    super(DefaultLibraryAliasValidator.REGEX, true, false);
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
