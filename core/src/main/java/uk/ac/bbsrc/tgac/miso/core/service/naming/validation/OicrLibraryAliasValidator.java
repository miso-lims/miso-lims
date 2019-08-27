package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.ListService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueTypeService;

public class OicrLibraryAliasValidator extends RegexValidator {

  private static final Logger log = LoggerFactory.getLogger(OicrLibraryAliasValidator.class);

  @Autowired
  private TissueOriginService tissueOriginService;
  @Autowired
  private TissueTypeService tissueTypeService;
  @Autowired
  private LibraryTypeService libraryTypeService;
  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;

  private boolean lookupsSuccessful;
  private Pattern pattern = null;

  public OicrLibraryAliasValidator() {
    super("", false, false, null);
  }

  @Override
  public Pattern getValidationPattern() {
    return pattern == null ? initializePattern() : pattern;
  }

  private synchronized Pattern initializePattern() {
    final String identityRegex = "(" + OicrProjectShortNameValidator.REGEX + ")_(\\d{3,}|\\d[CR]\\d{1,2})_"; // PROJ_0001_...
    final String tissueRegex = "(" + OicrSampleAliasValidator.TISSUE_NAME_REGEX + ")_(" + OicrSampleAliasValidator.TISSUE_NAME_REGEX + ")_"; // ...Pa_P_...
    final String designCodeRegex = "(" + "[A-Z]{2}" + "|\\?\\?)";
    final String illuminaRegex = "(" + "[A-Z]{2}" + "|\\?\\?)_(nn|\\d{2,6}|\\dK)_" + designCodeRegex;
    final String ontRegex = "(" + "[A-Z\\d]{3,4}" + ")_" + designCodeRegex + "_\\d+";
    final String pacbioRegex = "\\d{8}_\\d+"; // ...20170913_1
    String finalRegex = String.format("^%s(%s|%s(%s|%s))$", identityRegex, pacbioRegex, tissueRegex, illuminaRegex, ontRegex);

    return Pattern.compile(finalRegex);
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

  public void setTissueOriginService(TissueOriginService tissueOriginService) {
    this.tissueOriginService = tissueOriginService;
  }

  public void setTissueTypeService(TissueTypeService tissueTypeService) {
    this.tissueTypeService = tissueTypeService;
  }

  public void setLibraryDesignCodeService(LibraryDesignCodeService libraryDesignCodeService) {
    this.libraryDesignCodeService = libraryDesignCodeService;
  }

  public void setLibraryTypeService(LibraryTypeService libraryTypeService) {
    this.libraryTypeService = libraryTypeService;
  }

}
