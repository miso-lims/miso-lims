package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class V2LibraryAliasValidator extends RegexValidator {

  private static final String regex = "^[A-Z0-9]{3,10}_\\d{3,}_\\d{2,}_LB\\d{2,}$";

  public V2LibraryAliasValidator() {
    super(regex, false, false);
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
