package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class AssayTest implements Aliasable, Deletable, Serializable {

  public enum LibraryQualificationMethod {
    NONE("None"), ALIQUOT("Aliquot"), LOW_DEPTH_SEQUENCING("Low-depth Sequencing");

    private final String label;

    private LibraryQualificationMethod(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
  }

  public enum PermittedSamples {
    REQUISITIONED("Requisitioned"), SUPPLEMENTAL("Supplemental"), ALL("All");

    private final String label;

    private PermittedSamples(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
  }

  private static final long serialVersionUID = 1L;
  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long testId = UNSAVED_ID;

  private String alias;

  @ManyToOne(targetEntity = TissueTypeImpl.class)
  @JoinColumn(name = "tissueTypeId")
  private TissueType tissueType;

  private boolean negateTissueType;

  @ManyToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "extractionClassId")
  private SampleClass extractionClass;

  @ManyToOne
  @JoinColumn(name = "libraryDesignCodeId")
  private LibraryDesignCode libraryDesignCode;

  @Enumerated(EnumType.STRING)
  private LibraryQualificationMethod libraryQualificationMethod;

  @ManyToOne
  @JoinColumn(name = "libraryQualificationDesignCodeId")
  private LibraryDesignCode libraryQualificationDesignCode;

  private boolean repeatPerTimepoint;

  @Enumerated(EnumType.STRING)
  private PermittedSamples permittedSamples;

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String getDeleteType() {
    return "Assay Test";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public long getId() {
    return testId;
  }

  @Override
  public void setId(long id) {
    this.testId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public TissueType getTissueType() {
    return tissueType;
  }

  public void setTissueType(TissueType tissueType) {
    this.tissueType = tissueType;
  }

  public boolean isNegateTissueType() {
    return negateTissueType;
  }

  public void setNegateTissueType(boolean negateTissueType) {
    this.negateTissueType = negateTissueType;
  }

  public SampleClass getExtractionClass() {
    return extractionClass;
  }

  public void setExtractionClass(SampleClass extractionClass) {
    this.extractionClass = extractionClass;
  }

  public LibraryDesignCode getLibraryDesignCode() {
    return libraryDesignCode;
  }

  public void setLibraryDesignCode(LibraryDesignCode libraryDesignCode) {
    this.libraryDesignCode = libraryDesignCode;
  }

  public LibraryQualificationMethod getLibraryQualificationMethod() {
    return libraryQualificationMethod;
  }

  public void setLibraryQualificationMethod(LibraryQualificationMethod libraryQualificationMethod) {
    this.libraryQualificationMethod = libraryQualificationMethod;
  }

  public LibraryDesignCode getLibraryQualificationDesignCode() {
    return libraryQualificationDesignCode;
  }

  public void setLibraryQualificationDesignCode(LibraryDesignCode libraryQualificationDesignCode) {
    this.libraryQualificationDesignCode = libraryQualificationDesignCode;
  }

  public boolean isRepeatPerTimepoint() {
    return repeatPerTimepoint;
  }

  public void setRepeatPerTimepoint(boolean repeatPerTimepoint) {
    this.repeatPerTimepoint = repeatPerTimepoint;
  }

  public PermittedSamples getPermittedSamples() {
    return permittedSamples;
  }

  public void setPermittedSamples(PermittedSamples permittedSamples) {
    this.permittedSamples = permittedSamples;
  }

  @Override
  public boolean equals(Object o) {
    return LimsUtils.equals(this, o,
        AssayTest::isNegateTissueType,
        AssayTest::getAlias,
        AssayTest::getTissueType,
        AssayTest::getExtractionClass,
        AssayTest::getLibraryDesignCode,
        AssayTest::getLibraryQualificationMethod,
        AssayTest::getLibraryQualificationDesignCode,
        AssayTest::isRepeatPerTimepoint,
        AssayTest::getPermittedSamples);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, tissueType, negateTissueType, extractionClass, libraryDesignCode,
        libraryQualificationMethod, libraryQualificationDesignCode, repeatPerTimepoint, permittedSamples);
  }
}
