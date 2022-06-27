package uk.ac.bbsrc.tgac.miso.core.data.impl;

import uk.ac.bbsrc.tgac.miso.core.data.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class AssayTest implements Aliasable, Deletable, Serializable {

  public enum LibraryQualificationMethod {
    ALIQUOT("Aliquot"),
    LOW_DEPTH_SEQUENCING("Low-depth Sequencing");

    private final String label;

    private LibraryQualificationMethod(String label) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AssayTest assayTest = (AssayTest) o;
    return negateTissueType == assayTest.negateTissueType
        && Objects.equals(alias, assayTest.alias)
        && Objects.equals(tissueType, assayTest.tissueType)
        && Objects.equals(extractionClass, assayTest.extractionClass)
        && Objects.equals(libraryDesignCode, assayTest.libraryDesignCode)
        && libraryQualificationMethod == assayTest.libraryQualificationMethod
        && Objects.equals(libraryQualificationDesignCode, assayTest.libraryQualificationDesignCode)
        && repeatPerTimepoint == assayTest.repeatPerTimepoint;
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, tissueType, negateTissueType, extractionClass, libraryDesignCode,
        libraryQualificationMethod, libraryQualificationDesignCode, repeatPerTimepoint);
  }
}
