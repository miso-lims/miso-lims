package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class OicrLibraryAliasValidator extends RegexValidator {

  private static final String regex = "([A-Z\\d]{3,5})_([0-9]{3,4}|[0-9][CR][0-9]{1,2})_(nn|[A-Z]{1}[a-z]{1})_([nRPXMCFETOABS])_(SE|PE|MP|\\?\\?)_(nn|\\d{2,6}|\\dK)_(TS|EX|CH|BS|WG|TR|WT|SM|MR|AS|\\?\\?)";

  public OicrLibraryAliasValidator() {
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
