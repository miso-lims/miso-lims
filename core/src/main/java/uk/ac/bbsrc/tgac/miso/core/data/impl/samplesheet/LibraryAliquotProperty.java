package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import java.text.SimpleDateFormat;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;

public enum LibraryAliquotProperty {

  ALIAS("Alias") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getAlias();
    }
  },

  NAME("Name") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getName();
    }
  },

  INDEX_FAMILY("Index Family") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getParentLibrary().getIndex1() == null ? null
          : aliquot.getParentLibrary().getIndex1().getFamily().getName();
    }
  },

  INDEX_1_NAME("Index 1 Name") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getParentLibrary().getIndex1() == null ? null : aliquot.getParentLibrary().getIndex1().getName();
    }
  },

  INDEX_1_SEQUENCE("Index 1 Sequence") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getParentLibrary().getIndex1() == null ? null
          : aliquot.getParentLibrary().getIndex1().getSequence();
    }
  },

  INDEX_2_NAME("Index 2 Name") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getParentLibrary().getIndex2() == null ? null : aliquot.getParentLibrary().getIndex2().getName();
    }
  },

  INDEX_2_SEQUENCE("Index 2 Sequence") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getParentLibrary().getIndex2() == null ? null
          : aliquot.getParentLibrary().getIndex2().getSequence();
    }
  },

  BOX_ALIAS("Box Alias") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getBox() == null ? null : aliquot.getBox().getAlias();
    }
  },

  BOX_POSITION("Box Position") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getBoxPosition();
    }
  },

  PROJECT_CODE("Project Code") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getProjectCode();
    }
  },

  IDENTIFICATION_BARCODE("Matrix Barcode") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getAliquotBarcode();
    }
  },

  PROJECT_NAME("Project Name") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getProjectName();
    }
  },

  LIBRARY_DESCRIPTION("Library Description") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getLibraryDescription();
    }
  },

  DESIGN_CODE("Design Code") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      if (aliquot.getDesignCode() == null) {
        return null;
      }
      return "%s (%s)".formatted(aliquot.getDesignCode().getCode(), aliquot.getDesignCode().getDescription());
    }
  },

  TISSUE_ORIGIN("Tissue Origin") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getTissueAttributes() == null || aliquot.getTissueAttributes().getTissueOrigin() == null ? null
          : aliquot.getTissueAttributes().getTissueOrigin().getAlias();
    }
  },

  TISSUE_TYPE("Tissue Type") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getTissueAttributes() == null || aliquot.getTissueAttributes().getTissueType() == null ? null
          : aliquot.getTissueAttributes().getTissueType().getAlias();
    }
  },

  EXTERNAL_NAME("External Name") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getIdentityAttributes() == null ? null : aliquot.getIdentityAttributes().getExternalName();
    }
  },

  CREATED_DATE("Created Date") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getCreated() == null ? null : new SimpleDateFormat("yyyy-MM-dd").format(aliquot.getCreated());
    }
  },

  PLATFORM("Platform") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getParentLibrary().getPlatformType() == null ? null
          : aliquot.getParentLibrary().getPlatformType().getKey();
    }
  },

  SELECTION("Selection") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getParentLibrary().getLibrarySelectionType() == null ? null
          : aliquot.getParentLibrary().getLibrarySelectionType().getName();
    }
  },

  STRATEGY("Strategy") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getParentLibrary().getLibraryStrategyType() == null ? null
          : aliquot.getParentLibrary().getLibraryStrategyType().getName();
    }
  },

  HAS_UMIS("Has UMIs") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getParentLibrary().getUmis() ? "True" : "False";
    }
  },

  TARGETED_SEQUENCING("Targeted Sequencing") {
    @Override
    public String extract(ListLibraryAliquotView aliquot) {
      return aliquot.getTargetedSequencing() == null ? null : aliquot.getTargetedSequencing().getAlias();
    }
  };

  private final String label;

  private LibraryAliquotProperty(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public abstract String extract(ListLibraryAliquotView aliquot);
}
