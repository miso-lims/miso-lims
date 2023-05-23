package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.RegEx;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrSampleAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public abstract class OicrBaseLibraryAliasGenerator<T, R> implements NameGenerator<T> {

  private static final String SEPARATOR = "_";

  private static final @RegEx String sampleRegex = "^"
      + OicrSampleAliasValidator.IDENTITY_REGEX_PART
      + "(_(?:"
      + OicrSampleAliasValidator.TISSUE_NAME_REGEX
      + ")_"
      + OicrSampleAliasValidator.TISSUE_NAME_REGEX
      + ")_.*";
  private static final Pattern samplePattern = Pattern.compile(sampleRegex);

  @Autowired
  private SiblingNumberGenerator siblingNumberGenerator;

  public void setSiblingNumberGenerator(SiblingNumberGenerator siblingNumberGenerator) {
    this.siblingNumberGenerator = siblingNumberGenerator;
  }

  @Override
  public String generate(T item) throws MisoNamingException, IOException {
    if (!isDetailed(item)) {
      throw new IllegalArgumentException("Can only generate an alias for detailed samples");
    }
    @SuppressWarnings("unchecked")
    R detailedItem = (R) item;

    switch (getPlatformType(detailedItem)) {
      case ILLUMINA:
        return generateIlluminaLibraryAlias(detailedItem);
      case PACBIO:
        return generatePacBioLibraryAlias(detailedItem);
      case OXFORDNANOPORE:
        return generateOxfordNanoporeLibraryAlias(detailedItem);
      default:
        throw new MisoNamingException(
            "Alias generation is only available for Illumina, PacBio, and Oxford Nanopore Libraries");
    }
  }

  protected abstract boolean isDetailed(T item);

  protected abstract PlatformType getPlatformType(R item);

  protected abstract Sample getSample(R item);

  protected abstract LibraryType getLibraryType(R item);

  protected abstract Integer getDnaSize(R item);

  protected abstract LibraryDesignCode getLibraryDesignCode(R item);

  protected abstract LocalDate getCreationDate(R item);

  private String generateIlluminaLibraryAlias(R item) throws MisoNamingException {
    // e.g. PROJ_0001_Pa_P_PE_300_WG
    StringBuilder sb = new StringBuilder();
    sb.append(getIlluminaSampleAliasPart(item));
    sb.append(SEPARATOR).append(getLibraryTypeAbbreviation(item));
    sb.append(SEPARATOR).append(getInsertSize(item));
    sb.append(SEPARATOR).append(getDesignCode(item));
    return sb.toString();
  }

  /**
   * Get the beginning of the Sample alias, to be used in the Library alias, e.g. PCSI_0123_Pa_R
   * (project name, patient number, tissue origin, tissue type)
   * 
   * @param item the item that an alias is being generated for
   * @return the portion of the Sample alias to be used in the generated alias
   * @throws NullPointerException if parent Sample is not set
   * @throws MisoNamingException if unable to generate an alias for any other reason
   */
  private String getIlluminaSampleAliasPart(R item) throws MisoNamingException {
    Matcher m = getSampleAliasMatcher(item);
    return m.group(1) + "_" + m.group(2) + m.group(3);
  }

  private Matcher getSampleAliasMatcher(R item) throws MisoNamingException {
    Matcher m = samplePattern.matcher(getSample(item).getAlias());
    if (!m.matches()) {
      throw new MisoNamingException("Cannot generate alias due to non-standard alias on parent Sample");
    }
    return m;
  }

  /**
   * @param item the item that an alias is being generated for
   * @return the name portion representing the LibraryType
   * @throws NullPointerException if item's LibraryType is not set
   * @throws MisoNamingException if unable to generate an alias for any other reason
   */
  private String getLibraryTypeAbbreviation(R item) throws MisoNamingException {
    LibraryType libraryType = getLibraryType(item);
    String abbr = libraryType.getAbbreviation();
    if (abbr == null) {
      throw new MisoNamingException("Cannot generate alias for library type '" + libraryType.getDescription() + "'");
    }
    return abbr;
  }

  /**
   * @param item the item that an alias is being generated for
   * @return the name portion representing the estimated insert size
   * @throws MisoNamingException if item's dnaSize is not set
   */
  private String getInsertSize(R item) throws MisoNamingException {
    Integer dnaSize = getDnaSize(item);
    if (dnaSize == null) {
      throw new MisoNamingException("Cannot generate an alias without size (bp) set");
    }
    return dnaSize.toString();
  }

  /**
   * @param item the item that an alias is being generated for
   * @return the name portion representing the library design
   * @throws NullPointerException if item's LibraryDesignCode is missing
   */
  private String getDesignCode(R item) {
    LibraryDesignCode code = getLibraryDesignCode(item);
    if (code == null || code.getCode() == null) {
      throw new NullPointerException("Library design code missing");
    }
    return code.getCode();
  }

  private String generatePacBioLibraryAlias(R item) throws MisoNamingException, IOException {
    // e.g. PROJ_0001_20170913_1
    String partial = getIdentityAliasPart(item)
        + SEPARATOR
        + getCreationDateString(item)
        + SEPARATOR;
    int siblingNumber = siblingNumberGenerator.getNextSiblingNumber(LibraryImpl.class, partial);
    return partial + siblingNumber;
  }

  /**
   * Get the beginning of the Sample alias which should be the Identity, to be used in the generated
   * alias, e.g. PCSI_0123 (project name, patient number)
   * 
   * @param item the item that an alias is being generated for
   * @return the portion of the Sample alias to be used in the generated alias
   * @throws NullPointerException if item's parent Sample is not set
   * @throws MisoNamingException if unable to generate an alias for any other reason
   */
  private String getIdentityAliasPart(R item) throws MisoNamingException {
    Matcher m = getSampleAliasMatcher(item);
    return m.group(1) + "_" + m.group(2);
  }

  private String getCreationDateString(R item) {
    return LimsUtils.formatDate(getCreationDate(item));
  }

  private String generateOxfordNanoporeLibraryAlias(R item) throws MisoNamingException, IOException {
    // e.g. PROJ_0001_Pa_P_1D2_WG_1
    StringBuilder sb = new StringBuilder();
    sb.append(getIlluminaSampleAliasPart(item));
    sb.append(SEPARATOR).append(getLibraryTypeAbbreviation(item));
    sb.append(SEPARATOR).append(getDesignCode(item));
    sb.append(SEPARATOR);
    String partial = sb.toString();
    int siblingNumber = siblingNumberGenerator.getNextSiblingNumber(LibraryImpl.class, partial);
    return partial + siblingNumber;
  }

}
