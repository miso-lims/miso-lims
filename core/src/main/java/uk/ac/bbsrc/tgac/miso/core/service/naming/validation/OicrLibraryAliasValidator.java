package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import java.util.regex.Pattern;

public class OicrLibraryAliasValidator extends RegexValidator {

  static {
    final String identityRegex = "(" + OicrProjectCodeValidator.REGEX + ")_(\\d{3,}|\\d[CR]\\d{1,2})_"; // PROJ_0001_...
    final String tissueRegex =
        "(" + OicrSampleAliasValidator.TISSUE_NAME_REGEX + ")_(" + OicrSampleAliasValidator.TISSUE_NAME_REGEX + ")_"; // ...Pa_P_...
    final String designCodeRegex = "(" + "[A-Z]{2}" + "|\\?\\?)";
    final String illuminaRegex = "(" + "[A-Z]{2}" + "|\\?\\?)_(nn|\\d{2,6}|\\dK)_" + designCodeRegex;
    final String ontRegex = "(" + "[A-Z\\d]{3,4}" + ")_" + designCodeRegex + "_\\d+";
    final String pacbioRegex = "\\d{8}_\\d+"; // ...20170913_1
    String finalRegex =
        String.format("^%s(%s|%s(%s|%s))$", identityRegex, pacbioRegex, tissueRegex, illuminaRegex, ontRegex);

    pattern = Pattern.compile(finalRegex);
  }

  private static final Pattern pattern;

  public OicrLibraryAliasValidator() {
    super("", false, false);
  }

  @Override
  public Pattern getValidationPattern() {
    return pattern;
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
