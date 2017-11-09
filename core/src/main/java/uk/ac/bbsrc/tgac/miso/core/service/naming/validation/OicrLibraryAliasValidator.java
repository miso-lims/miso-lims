package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import javax.annotation.RegEx;

public class OicrLibraryAliasValidator extends RegexValidator {

  // PROJ_0001_...
  private static final String COMMON_PREFIX = "([A-Z\\d]{3,5})_(\\d{3,6}|\\d[CR]\\d{1,2})_";
  // ...Pa_P...
  private static final String TISSUE_MID = "(nn|[A-Z][a-z])_([A-Zn])_";

  private static final String DESIGN_CODE = "(TS|EX|CH|BS|WG|TR|WT|SM|MR|AS|\\\\?\\\\?)";
  // ...PE_700_WG
  private static final String ILLUMINA_REGEX = "(SE|PE|MP|TR|\\?\\?)_(nn|\\d{2,6}|\\dK)_" + DESIGN_CODE;
  // ...1D2_WG_1
  private static final String OXFORD_NANOPORE_REGEX = "(LIG|1D2|RNA|LOW|RPD|CDNA)_" + DESIGN_CODE + "_\\d+";
  // ...20170913_1
  private static final String PACBIO_REGEX = "\\d{8}_\\d+";
  // PROJ_0001_Pa_P_PE_700_WG (Illumina) or PROJ_0001_20170913_1 (PacBio)
  private static final @RegEx String REGEX = String.format("^%s(%s|%s(%s|%s))$", COMMON_PREFIX, PACBIO_REGEX, TISSUE_MID, ILLUMINA_REGEX,
      OXFORD_NANOPORE_REGEX);

  public OicrLibraryAliasValidator() {
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
