package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public class OicrLibraryAliasGenerator implements NameGenerator<Library> {

  private static final String sampleRegex = "^([A-Z\\d]{3,5}_\\d{3,6}_(?:[A-Z][a-z]|nn)_[A-Zn])_.*";
  private static final Pattern samplePattern = Pattern.compile(sampleRegex);

  @Override
  public String generate(Library library) throws MisoNamingException {
    if (!isDetailedLibrary(library)) {
      throw new IllegalArgumentException("Can only generate an alias for detailed samples");
    }
    DetailedLibrary detailed = (DetailedLibrary) library;

    switch (detailed.getPlatformType()) {
    case ILLUMINA:
      return generateIlluminaLibraryAlias(detailed);
    case PACBIO:
      return generatePacBioLibraryAlias(detailed);
    default:
      throw new MisoNamingException("Alias generation is only available for Illumina and PacBio Libraries");
    }
  }

  private String generateIlluminaLibraryAlias(DetailedLibrary library) throws MisoNamingException {
    // e.g. PROJ_0001_Pa_P_PE_300_WG
    StringBuilder sb = new StringBuilder();
    sb.append(getIlluminaSampleAliasPart(library));
    sb.append("_").append(getLibraryTypeAbbreviation(library));
    sb.append("_").append(getInsertSize(library));
    sb.append("_").append(getDesignCode(library));
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
    Matcher m = samplePattern.matcher(library.getSample().getAlias());
    if (!m.matches()) {
      throw new MisoNamingException("Cannot generate alias due to non-standard alias on parent Sample");
    }
    return m.group(1);
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

  private String generatePacBioLibraryAlias(DetailedLibrary library) throws MisoNamingException {
    // e.g. PROJ_1_150pM
    StringBuilder sb = new StringBuilder();
    sb.append(getProjectShortName(library));
    sb.append("_").append(getBatchNumber(library));
    sb.append("_").append(getConcentration(library));
    return sb.toString();
  }

  /**
   * @param library the Library that an alias is being generated for
   * @return the project's shortName
   * @throws NullPointerException if the project or its shortName are missing
   */
  private String getProjectShortName(Library library) {
    if (library.getSample().getProject() == null || library.getSample().getProject().getShortName() == null) {
      throw new NullPointerException("Project or shortName missing");
    }
    return library.getSample().getProject().getShortName().toString();
  }

  /**
   * @param library the Library that an alias is being generated for
   * @return the parent tissue's timesReceived
   * @throws IllegalArgumentException if no related SampleTissue is found
   * @throws NullPointerException if the tissue sample's timesReceived is missing
   */
  private String getBatchNumber(DetailedLibrary library) {
    for (DetailedSample sample = (DetailedSample) library.getSample(); sample != null; sample = sample.getParent()) {
      if (isTissueSample(sample)) {
        SampleTissue tissue = (SampleTissue) deproxify(sample);
        return tissue.getTimesReceived().toString();
      }
    }
    throw new IllegalStateException("No Tissue sample found in hierarchy");
  }

  /**
   * @param library the Library that an alias is being generated for
   * @return the concentration as a String including the units
   * @throws NullPointerException if PlatformType is missing
   * @throws MisoNamingException if concentration is not set
   */
  private String getConcentration(DetailedLibrary library) throws MisoNamingException {
    if (library.getInitialConcentration() == null) {
      throw new MisoNamingException("Cannot generate an alias without concentration set");
    }
    return library.getInitialConcentration().intValue() + library.getPlatformType().getLibraryConcentrationUnits();
  }

}
