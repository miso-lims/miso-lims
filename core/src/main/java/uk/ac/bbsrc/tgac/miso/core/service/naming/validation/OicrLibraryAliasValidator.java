package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.core.store.TissueTypeDao;

public class OicrLibraryAliasValidator extends RegexValidator {

  private static final Logger log = LoggerFactory.getLogger(OicrLibraryAliasValidator.class);

  @Autowired
  private TissueOriginDao tissueOriginDao;
  @Autowired
  private TissueTypeDao tissueTypeDao;
  @Autowired
  private LibraryStore libraryStore;
  @Autowired
  private LibraryDesignCodeDao libraryDesignCodeDao;

  private Pattern pattern = null;

  public OicrLibraryAliasValidator() {
    super("", false, false);
  }

  @Override
  public Pattern getValidationPattern() {
    return pattern == null ? initializePattern() : pattern;
  }

  private Pattern initializePattern() {
    boolean success = true;
    final String identityRegex = "([A-Z\\d]{3,5})_(\\d{3,6}|\\d[CR]\\d{1,2})_"; // PROJ_0001_...

    String origins = makeOptionRegex(tissueOriginDao.getTissueOrigin(), TissueOrigin::getAlias);
    String tissueTypes = makeOptionRegex(tissueTypeDao.getTissueType(), TissueType::getAlias);
    final String tissueRegex = "(" + origins + ")_(" + tissueTypes + ")_"; // ...Pa_P_...

    String designCodes = null;
    try {
      designCodes = makeOptionRegex(libraryDesignCodeDao.getLibraryDesignCodes(), LibraryDesignCode::getCode);
    } catch (IOException e) {
      success = false;
      log.error("LibraryDesignCode lookup failed. Falling back to simple validation pattern", e);
      designCodes = "[A-Z]{2}";
    }
    final String designCodeRegex = "(" + designCodes + "|\\?\\?)";

    String illuminaLibTypes = null;
    try {
      illuminaLibTypes = makeOptionRegex(libraryStore.listLibraryTypesByPlatform(PlatformType.ILLUMINA), LibraryType::getAbbreviation);
    } catch (IOException e) {
      success = false;
      log.error("Illumina LibraryType lookup failed. Falling back to simple validation pattern", e);
      illuminaLibTypes = "[A-Z]{2}";
    }
    final String illuminaRegex = "(" + illuminaLibTypes + "|\\?\\?)_(nn|\\d{2,6}|\\dK)_" + designCodeRegex; // ...PE_700_WG

    String ontLibTypes = null;
    try {
      ontLibTypes = makeOptionRegex(libraryStore.listLibraryTypesByPlatform(PlatformType.OXFORDNANOPORE), LibraryType::getAbbreviation);
    } catch (IOException e) {
      success = false;
      log.error("Oxford Nanopore LibraryType lookup failed. Falling back to simple validation pattern", e);
      ontLibTypes = "[A-Z\\d]{3,4}";
    }
    final String ontRegex = "(" + ontLibTypes + ")_" + designCodeRegex + "_\\d+"; // ...1D2_WG_1

    final String pacbioRegex = "\\d{8}_\\d+"; // ...20170913_1

    // PROJ_0001_Pa_P_PE_700_WG (Illumina) or PROJ_0001_20170913_1 (PacBio) or PROJ_0001_Pa_P_1D2_WG_1 (Oxford Nanopore)
    String finalRegex = String.format("^%s(%s|%s(%s|%s))$", identityRegex, pacbioRegex, tissueRegex, illuminaRegex, ontRegex);

    if (success) {
      // Save if everything initialized properly. This means the lookups aren't required every time,
      // but also that MISO must be restarted to update values for validation
      pattern = Pattern.compile(finalRegex);
    }
    return pattern;
  }

  private static <T> String makeOptionRegex(Collection<T> items, Function<T, String> mapFunction) {
    return String.join("|", items.stream().map(mapFunction).collect(Collectors.toSet()));
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

  public void setTissueOriginDao(TissueOriginDao tissueOriginDao) {
    this.tissueOriginDao = tissueOriginDao;
  }

  public void setTissueTypeDao(TissueTypeDao tissueTypeDao) {
    this.tissueTypeDao = tissueTypeDao;
  }

  public void setLibraryStore(LibraryStore libraryStore) {
    this.libraryStore = libraryStore;
  }

  public void setLibraryDesignCodeDao(LibraryDesignCodeDao libraryDesignCodeDao) {
    this.libraryDesignCodeDao = libraryDesignCodeDao;
  }

}
