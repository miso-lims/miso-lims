package ca.on.oicr.pinery.lims.miso.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleTypeConverter {

  private static final Logger log = LoggerFactory.getLogger(SampleTypeConverter.class);

  private static final String MISO_TYPE_LIBRARY = "Library";
  private static final String MISO_TYPE_DILUTION = "Dilution";

  private static final String PLATFORM_ILLUMINA = "ILLUMINA";

  private static final String LIBRARY_TYPE_MRNA = "mRNA Seq";
  private static final String LIBRARY_TYPE_PAIRED_END = "Paired End";
  private static final String LIBRARY_TYPE_SMALL_RNA = "Small RNA";
  private static final String LIBRARY_TYPE_SINGLE_END = "Single End";
  private static final String LIBRARY_TYPE_WHOLE_TRANSCRIPTOME = "Whole Transcriptome";
  private static final String LIBRARY_TYPE_MATE_PAIR = "Mate Pair";
  private static final String LIBRARY_TYPE_TOTAL_RNA = "Total RNA";

  private static enum IlluminaSampleType {

    SE("Illumina SE Library", "Illumina SE Library Seq"),
    PE("Illumina PE Library", "Illumina PE Library Seq"),
    SM_RNA("Illumina smRNA Library", "Illumina smRNA Library Seq"),
    M_RNA("Illumina mRNA Library", "Illumina mRNA Library Seq"),
    WT("Illumina WT Library", "Illumina WT Library Seq"),
    MP("Illumina MP Library", "Illumina MP Library Seq"),
    TOTAL_RNA("Illumina_totalRNA_Library", "Illumina_totalRNA_Library_Seq");

    private final String libraryType;
    private final String dilutionType;

    private IlluminaSampleType(String libraryType, String dilutionType) {
      this.libraryType = libraryType;
      this.dilutionType = dilutionType;
    }

    public String getLibraryType() {
      return libraryType;
    }

    public String getDilutionType() {
      return dilutionType;
    }
  }

  private static final String SAMPLE_TYPE_UNKNOWN = "Unknown";

  /**
   * Translates a MISO Sample Class to a Pinery Sample Type
   * 
   * @param misoType SampleClass alias from MISO
   * @return the corresponding Pinery Sample Type String
   */
  public static String getSampleType(String misoSampleClass) {
    return misoSampleClass
        .replace(" (stock)", "")
        .replace(" (aliquot)", "")
        .replace("LCM Tube", "LCM Tubes");
  }

  /**
   * Determines the correct Pinery Sample Type to assign to a MISO Library of Library Dilution
   * 
   * @param misoType "Library" or "Dilution"
   * @param platformName MISO LibraryType platformType
   * @param libraryType MISO LibraryType description
   * @return The Pinery SampleType String that corresponds to the parameters given, or "Unknown" if it cannot be determined
   */
  public static String getNonSampleSampleType(String misoType, String platformName, String libraryType) {
    if (platformName == null) {
      log.debug("Cannot determine SampleType due to null platformName");
      return SAMPLE_TYPE_UNKNOWN;
    }

    switch (platformName) {
    case PLATFORM_ILLUMINA:
      IlluminaSampleType sType = null;
      if (libraryType == null) {
        log.debug("Cannot determine SampleType due to null libraryType");
        return SAMPLE_TYPE_UNKNOWN;
      }

      switch (libraryType) {
      case LIBRARY_TYPE_MRNA:
        sType = IlluminaSampleType.M_RNA;
        break;
      case LIBRARY_TYPE_PAIRED_END:
        sType = IlluminaSampleType.PE;
        break;
      case LIBRARY_TYPE_SMALL_RNA:
        sType = IlluminaSampleType.SM_RNA;
        break;
      case LIBRARY_TYPE_SINGLE_END:
        sType = IlluminaSampleType.SE;
        break;
      case LIBRARY_TYPE_WHOLE_TRANSCRIPTOME:
        sType = IlluminaSampleType.WT;
        break;
      case LIBRARY_TYPE_MATE_PAIR:
        sType = IlluminaSampleType.MP;
        break;
      case LIBRARY_TYPE_TOTAL_RNA:
        sType = IlluminaSampleType.TOTAL_RNA;
        break;
      default:
        log.debug("Unexpected LibraryType: " + libraryType + ", Cannot determine Sample Type");
        return SAMPLE_TYPE_UNKNOWN;
      }

      switch (misoType) {
      case MISO_TYPE_LIBRARY:
        return sType.getLibraryType();
      case MISO_TYPE_DILUTION:
        return sType.getDilutionType();
      default:
        log.debug("Unexpected MISO type: " + misoType + ". Cannot determine Sample Type");
        return SAMPLE_TYPE_UNKNOWN;
      }
    default:
      log.debug("Unknown platform: " + platformName + ". Cannot determine Sample Type");
      return SAMPLE_TYPE_UNKNOWN;
    }
  }

}
