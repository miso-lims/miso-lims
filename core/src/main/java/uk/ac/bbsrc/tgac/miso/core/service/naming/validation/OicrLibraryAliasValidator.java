package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import javax.annotation.RegEx;

public class OicrLibraryAliasValidator extends RegexValidator {

  // PROJ_0001_...
  private static final String commonPrefix = "([A-Z\\d]{3,5})_(\\d{3,6}|\\d[CR]\\d{1,2})_";
  // ...Pa_P_PE_700_WG
  private static final String illuminaRegex = "(nn|[A-Z][a-z])_([A-Zn])_(SE|PE|MP|TR|\\?\\?)_(nn|\\d{2,6}|\\dK)_(TS|EX|CH|BS|WG|TR|WT|SM|MR|AS|\\?\\?)";
  // ...20170913_1
  private static final String pacBioRegex = "\\d{8}_\\d+";
  // PROJ_0001_Pa_P_PE_700_WG (Illumina) or PROJ_0001_20170913_1 (PacBio)
  private static final @RegEx String regex = String.format("^%s(%s|%s)$", commonPrefix, illuminaRegex, pacBioRegex);

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
