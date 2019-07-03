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
    lookupsSuccessful = true;
    final String identityRegex = "(" + OicrProjectShortNameValidator.REGEX + ")_(\\d{3,}|\\d[CR]\\d{1,2})_"; // PROJ_0001_...

    String origins = makeOptionRegex("Tissue origins", tissueOriginService, null, TissueOrigin::getAlias, "[A-Z][a-z]");
    String tissueTypes = makeOptionRegex("Tissue types", tissueTypeService, null, TissueType::getAlias, "[A-Z]");

    final String tissueRegex = "(" + origins + ")_(" + tissueTypes + ")_"; // ...Pa_P_...

    String designCodes = makeOptionRegex("Library design codes", libraryDesignCodeService, null, LibraryDesignCode::getCode, "[A-Z]{2}");
    final String designCodeRegex = "(" + designCodes + "|\\?\\?)";

    String illuminaLibTypes = makeOptionRegex("Library types", libraryTypeService, lt -> lt.getPlatformType() == PlatformType.ILLUMINA,
        LibraryType::getAbbreviation, "[A-Z]{2}");
    final String illuminaRegex = "(" + illuminaLibTypes + "|\\?\\?)_(nn|\\d{2,6}|\\dK)_" + designCodeRegex; // ...PE_700_WG

    String ontLibTypes = makeOptionRegex("Library types", libraryTypeService, lt -> lt.getPlatformType() == PlatformType.OXFORDNANOPORE,
        LibraryType::getAbbreviation, "[A-Z\\d]{3,4}");
    final String ontRegex = "(" + ontLibTypes + ")_" + designCodeRegex + "_\\d+"; // ...1D2_WG_1

    final String pacbioRegex = "\\d{8}_\\d+"; // ...20170913_1

    // PROJ_0001_Pa_P_PE_700_WG (Illumina) or PROJ_0001_20170913_1 (PacBio) or PROJ_0001_Pa_P_1D2_WG_1 (Oxford Nanopore)
    String finalRegex = String.format("^%s(%s|%s(%s|%s))$", identityRegex, pacbioRegex, tissueRegex, illuminaRegex, ontRegex);

    if (lookupsSuccessful) {
      // Save if everything initialized properly. This means the lookups aren't required every time,
      // but also that MISO must be restarted to update values for validation
      pattern = Pattern.compile(finalRegex);
    }
    return pattern;
  }

  private <T extends Identifiable> String makeOptionRegex(String type, ListService<T> service, Predicate<T> filter,
      Function<T, String> mapFunction, String simpleRegex) {
    try {
      return service.list().stream()
          .filter(filter == null ? (i -> true) : filter)
          .map(mapFunction)
          .collect(Collectors.joining("|"));
    } catch (IOException e) {
      lookupsSuccessful = false;
      log.error("{} lookup failed. Falling back to simple validation pattern", type);
      return simpleRegex;
    }
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
