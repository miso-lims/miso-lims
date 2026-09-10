package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import java.text.SimpleDateFormat;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum LibraryAliquotProperty {

  ALIAS("Alias") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getAlias();
    }
  },

  NAME("Name") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getName();
    }
  },

  INDEX_FAMILY("Index Family") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getIndex1() == null ? null
          : aliquot.getLibrary().getIndex1().getFamily().getName();
    }
  },

  INDEX_1_NAME("Index 1 Name") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getIndex1() == null ? null : aliquot.getLibrary().getIndex1().getName();
    }
  },

  INDEX_1_SEQUENCE("Index 1 Sequence") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getIndex1() == null ? null : aliquot.getLibrary().getIndex1().getSequence();
    }
  },

  INDEX_2_NAME("Index 2 Name") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getIndex2() == null ? null : aliquot.getLibrary().getIndex2().getName();
    }
  },

  INDEX_2_SEQUENCE("Index 2 Sequence") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getIndex2() == null ? null : aliquot.getLibrary().getIndex2().getSequence();
    }
  },

  BOX_ALIAS("Box Alias") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getBox() == null ? null : aliquot.getBox().getAlias();
    }
  },

  BOX_POSITION("Box Position") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getBoxPosition();
    }
  },

  PROJECT_CODE("Project Code") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getSample().getProject().getCode();
    }
  },

  IDENTIFICATION_BARCODE("Matrix Barcode") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getIdentificationBarcode();
    }
  },

  PROJECT_TITLE("Project Name") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getSample().getProject().getTitle();
    }
  },

  LIBRARY_ALIQUOT_DESCRIPTION("Library Aliquot Description") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getDescription();
    }
  },

  DESIGN_CODE("Design Code") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      if (!(aliquot instanceof DetailedLibraryAliquot detailedAliquot)
          || detailedAliquot.getLibraryDesignCode() == null) {
        return null;
      }
      return detailedAliquot.getLibraryDesignCode().getCode();
    }
  },

  DESIGN_CODE_DESCRIPTION("Design Code Description") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      if (!(aliquot instanceof DetailedLibraryAliquot detailedAliquot)
          || detailedAliquot.getLibraryDesignCode() == null) {
        return null;
      }
      return detailedAliquot.getLibraryDesignCode().getDescription();
    }
  },

  TISSUE_ORIGIN("Tissue Origin") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      TissueOrigin tissueOrigin = getTissueOrigin(aliquot);
      return tissueOrigin == null ? null : tissueOrigin.getAlias();
    }
  },

  TISSUE_ORIGIN_DESCRIPTION("Tissue Origin Description") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      TissueOrigin tissueOrigin = getTissueOrigin(aliquot);
      return tissueOrigin == null ? null : tissueOrigin.getDescription();
    }
  },

  TISSUE_TYPE("Tissue Type") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      TissueType tissueType = getTissueType(aliquot);
      return tissueType == null ? null : tissueType.getAlias();
    }
  },

  TISSUE_TYPE_DESCRIPTION("Tissue Type Description") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      TissueType tissueType = getTissueType(aliquot);
      return tissueType == null ? null : tissueType.getDescription();
    }
  },

  EXTERNAL_NAME("External Name") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      Sample sample = aliquot.getLibrary().getSample();
      SampleIdentity identity = null;
      if (sample instanceof SampleIdentity si) {
        identity = si;
      } else if (LimsUtils.isDetailedSample(sample)) {
        identity = LimsUtils.getParent(SampleIdentity.class, (DetailedSample) sample);
      }
      return identity == null ? null : identity.getExternalName();
    }
  },

  CREATED_DATE("Created Date") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getCreationTime() == null ? null
          : new SimpleDateFormat("yyyy-MM-dd").format(aliquot.getCreationTime());
    }
  },

  PLATFORM("Platform") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getPlatformType() == null ? null
          : aliquot.getLibrary().getPlatformType().getKey();
    }
  },

  SELECTION("Selection") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getLibrarySelectionType() == null ? null
          : aliquot.getLibrary().getLibrarySelectionType().getName();
    }
  },

  STRATEGY("Strategy") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getLibraryStrategyType() == null ? null
          : aliquot.getLibrary().getLibraryStrategyType().getName();
    }
  },

  HAS_UMIS("Has UMIs") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getUmis() ? "True" : "False";
    }
  },

  TARGETED_SEQUENCING("Targeted Sequencing") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getTargetedSequencing() == null ? null : aliquot.getTargetedSequencing().getAlias();
    }
  },

  SAMPLE_TYPE("Sample Type") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getSample().getSampleType();
    }
  },

  SCIENTIFIC_NAME("Scientific Name") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      return aliquot.getLibrary().getSample().getScientificName().getAlias();

    }
  },

  SAMPLE_INDEX("Sample Index") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      Sample sample = aliquot.getLibrary().getSample();
      if (!LimsUtils.isDetailedSample(sample)) {
        return null;
      }
      SampleTissueProcessing tissueProcessing =
          LimsUtils.getParentOrSelf(SampleTissueProcessing.class, (DetailedSample) sample);
      return tissueProcessing == null || tissueProcessing.getIndex() == null ? null
          : tissueProcessing.getIndex().getName();
    }
  },

  SLIDE_IDENTIFICATION_BARCODE("Slide Matrix ID") {
    @Override
    public String extract(LibraryAliquot aliquot) {
      Sample sample = aliquot.getLibrary().getSample();
      if (!LimsUtils.isDetailedSample(sample)) {
        return null;
      }
      SampleSlide slide = LimsUtils.getParentOrSelf(SampleSlide.class, (DetailedSample) sample);
      return slide == null ? null : slide.getIdentificationBarcode();
    }
  };

  private final String label;

  private LibraryAliquotProperty(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public abstract String extract(LibraryAliquot aliquot);

  private static TissueOrigin getTissueOrigin(LibraryAliquot aliquot) {
    Sample sample = aliquot.getLibrary().getSample();
    if (!LimsUtils.isDetailedSample(sample) || ((DetailedSample) sample).getTissueAttributes() == null) {
      return null;
    }
    return ((DetailedSample) sample).getTissueAttributes().getTissueOrigin();
  }

  private static TissueType getTissueType(LibraryAliquot aliquot) {
    Sample sample = aliquot.getLibrary().getSample();
    if (!LimsUtils.isDetailedSample(sample) || ((DetailedSample) sample).getTissueAttributes() == null) {
      return null;
    }
    return ((DetailedSample) sample).getTissueAttributes().getTissueType();
  }
}
