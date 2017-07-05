package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class OicrLibraryAliasValidator extends RegexValidator {

  private static final String commonPrefix = "([A-Z\\d]{3,5})_";
  private static final String illuminaRegex = "(\\d{3,}|\\d[CR]\\d{1,2})_(nn|[A-Z][a-z])_([A-Zn])_(SE|PE|MP|TR|\\?\\?)_(nn|\\d{2,6}|\\dK)_(TS|EX|CH|BS|WG|TR|WT|SM|MR|AS|\\?\\?)";
  private static final String pacBioRegex = "(\\d{1,2})_(\\d+)pM";
  private static final String regex = String.format("^%s(%s|%s)$", commonPrefix, illuminaRegex, pacBioRegex);

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
