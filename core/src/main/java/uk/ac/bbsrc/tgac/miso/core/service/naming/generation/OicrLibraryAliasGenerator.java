package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isDetailedLibrary;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.RegEx;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;

public class OicrLibraryAliasGenerator implements NameGenerator<Library> {

  private static final String SEPARATOR = "_";

  private static final @RegEx String sampleRegex = "^([A-Z\\d]{3,5}_\\d{3,6})(_(?:[A-Z][a-z]|nn)_[A-Zn])_.*";
  private static final Pattern samplePattern = Pattern.compile(sampleRegex);

  @Autowired
  private SiblingNumberGenerator siblingNumberGenerator;

  public void setSiblingNumberGenerator(SiblingNumberGenerator siblingNumberGenerator) {
    this.siblingNumberGenerator = siblingNumberGenerator;
  }

  @Override
  public String generate(Library library) throws MisoNamingException, IOException {
    if (!isDetailedLibrary(library)) {
      throw new IllegalArgumentException("Can only generate an alias for detailed samples");
    }
    DetailedLibrary detailed = (DetailedLibrary) library;

    switch (detailed.getPlatformType()) {
    case ILLUMINA:
      return generateIlluminaLibraryAlias(detailed);
    case PACBIO:
      return generatePacBioLibraryAlias(detailed);
    case OXFORDNANOPORE:
      return generateOxfordNanoporeLibraryAlias(detailed);
    default:
      throw new MisoNamingException("Alias generation is only available for Illumina and PacBio Libraries");
    }
  }

  private String generateIlluminaLibraryAlias(DetailedLibrary library) throws MisoNamingException {
    // e.g. PROJ_0001_Pa_P_PE_300_WG
    StringBuilder sb = new StringBuilder();
    sb.append(getIlluminaSampleAliasPart(library));
    sb.append(SEPARATOR).append(getLibraryTypeAbbreviation(library));
    sb.append(SEPARATOR).append(getInsertSize(library));
    sb.append(SEPARATOR).append(getDesignCode(library));
    return sb.toString();
  }

  /**
   * Get the beginning of the Sample alias, to be used in the Library alias,
   * e.g. PCSI_0123_Pa_R (project name, patient number, tissue origin, tissue type)
   * 
   * @param library the Library that an alias is being generated for
   * @return the portion of the Sample alias to be used in the Library alias
   * @throws NullPointerException if library's parent Sample is not set
   * @throws MisoNamingException if unable to generate an alias for any other reason
   */
  private String getIlluminaSampleAliasPart(DetailedLibrary library) throws MisoNamingException {
    Matcher m = getSampleAliasMatcher(library);
    return m.group(1) + m.group(2);
  }

  private Matcher getSampleAliasMatcher(DetailedLibrary library) throws MisoNamingException {
    Matcher m = samplePattern.matcher(library.getSample().getAlias());
    if (!m.matches()) {
      throw new MisoNamingException("Cannot generate alias due to non-standard alias on parent Sample");
    }
    return m;
  }
  
  /**
   * @param library the Library that an alias is being generated for
   * @return the name portion representing the LibraryType
   * @throws NullPointerException if library's LibraryType is not set
   * @throws MisoNamingException if unable to generate an alias for any other reason
   */
  private String getLibraryTypeAbbreviation(DetailedLibrary library) throws MisoNamingException {
    String abbr = library.getLibraryType().getAbbreviation();
    if (abbr == null) {
      throw new MisoNamingException("Cannot generate alias for LibraryType '" + library.getLibraryType().getDescription() + "'");
    }
    return abbr;
  }

  /**
   * @param library the Library that an alias is being generated for
   * @return the name portion represeting the estimated insert size
   * @throws MisoNamingException if library's dnaSize is not set
   */
  private String getInsertSize(Library library) throws MisoNamingException {
    if (library.getDnaSize() == null) {
      throw new MisoNamingException("Cannot generate an alias without insert size set");
    }
    return library.getDnaSize().toString();
  }

  /**
   * @param library the Library that an alias is being generated for
   * @return the name portion represeting the library design
   * @throws NullPointerException if library's LibraryDesignCode is missing
   */
  private String getDesignCode(DetailedLibrary library) {
    if (library.getLibraryDesignCode() == null || library.getLibraryDesignCode().getCode() == null) {
      throw new NullPointerException("LibraryDesignCode missing");
    }
    return library.getLibraryDesignCode().getCode();
  }

  private String generatePacBioLibraryAlias(DetailedLibrary library) throws MisoNamingException, IOException {
    // e.g. PROJ_0001_20170913_1
    String partial = getIdentityAliasPart(library)
        + SEPARATOR
        + getCreationDateString(library)
        + SEPARATOR;
    int siblingNumber = siblingNumberGenerator.getNextSiblingNumber(LibraryImpl.class, partial);
    return partial + siblingNumber;
  }

  /**
   * Get the beginning of the Sample alias which should be the Identity, to be used in the Library alias,
   * e.g. PCSI_0123 (project name, patient number)
   * 
   * @param library the Library that an alias is being generated for
   * @return the portion of the Sample alias to be used in the Library alias
   * @throws NullPointerException if library's parent Sample is not set
   * @throws MisoNamingException if unable to generate an alias for any other reason
   */
  private String getIdentityAliasPart(DetailedLibrary library) throws MisoNamingException {
    Matcher m = getSampleAliasMatcher(library);
    return m.group(1);
  }

  private String getCreationDateString(DetailedLibrary library) {
    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    return df.format(library.getCreationDate());
  }

  private String generateOxfordNanoporeLibraryAlias(DetailedLibrary library) throws MisoNamingException, IOException {
    // e.g. PROJ_0001_Pa_P_1D2_WG_1
    StringBuilder sb = new StringBuilder();
    sb.append(getIlluminaSampleAliasPart(library));
    sb.append(SEPARATOR).append(getLibraryTypeAbbreviation(library));
    sb.append(SEPARATOR).append(getDesignCode(library));
    sb.append(SEPARATOR);
    String partial = sb.toString();
    int siblingNumber = siblingNumberGenerator.getNextSiblingNumber(LibraryImpl.class, partial);
    return partial + siblingNumber;
  }

}
