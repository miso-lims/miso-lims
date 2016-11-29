package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class OicrSampleAliasValidator extends RegexValidator {

  private static final String IDENTITY_REGEX_PART = "([A-Z\\d]{3,5})_(\\d{3,5})";
  private static final String TISSUE_ORIGIN_REGEX = "(Ad|Ap|Ag|As|Bm|Bn|Br|Bu|Cb|Cn|Du|Es|Fs|Gb|Hr|Ki|Le|Li|Ln|Lu|Lv|Lx|Ly|Md|Me|Nk|Oc|Om|Ov|Pa|Pb|Pr|Sa|Sg|Si|Sk|Sm|Sp|St|Ta|Tr|Mu|Wm|nn)";
  private static final String TISSUE_TYPE_REGEX = "[BRPXMCFESATOn]";
  private static final String TISSUE_REGEX_PART = TISSUE_ORIGIN_REGEX + "_" + TISSUE_TYPE_REGEX + "_(nn|\\d{2})_(\\d{1,2})-(\\d{1,2})";
  private static final String ANALYTE_REGEX_PART = "(C|CV|HE|LCM|D_S|R_S|D_|R_(\\d+_(MR|SM|WT)_)?)\\d+";

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
