package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;

@Entity
@Table(name = "LibraryDesign")
public class LibraryDesign {
  public static boolean validate(Library library, Iterable<LibraryDesign> rules) {
    // Return true if the ruleset is empty.
    boolean first = true;
    for (LibraryDesign rule : rules) {
      if (rule.validate(library)) return true;
      first = false;
    }
    return first;
  }

  @Id
  private Long libraryDesignId;

  @ManyToOne
  @JoinColumn(name = "librarySelectionType")
  private LibrarySelectionType librarySelectionType;

  @ManyToOne
  @JoinColumn(name = "libraryStrategyType")
  private LibraryStrategyType libraryStrategyType;
  @Column(nullable = false)
  private String name;
  @ManyToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "sampleClassId", nullable = false)
  private SampleClass sampleClass;

  @ManyToOne(targetEntity = LibraryDesignCode.class)
  @JoinColumn(name = "libraryDesignCodeId")
  private LibraryDesignCode libraryDesignCode;

  public Long getId() {
    return libraryDesignId;
  }

  public LibrarySelectionType getLibrarySelectionType() {
    return librarySelectionType;
  }

  public LibraryStrategyType getLibraryStrategyType() {
    return libraryStrategyType;
  }

  public String getName() {
    return name;
  }

  public SampleClass getSampleClass() {
    return sampleClass;
  }

  public LibraryDesignCode getLibraryDesignCode() {
    return libraryDesignCode;
  }

  public void setId(Long libraryDesignId) {
    this.libraryDesignId = libraryDesignId;
  }

  public void setLibrarySelectionType(LibrarySelectionType librarySelectionType) {
    this.librarySelectionType = librarySelectionType;
  }

  public void setLibraryStrategyType(LibraryStrategyType libraryStrategyType) {
    this.libraryStrategyType = libraryStrategyType;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSampleClass(SampleClass sampleClass) {
    this.sampleClass = sampleClass;
  }

  public void setLibraryDesignCode(LibraryDesignCode libraryDesignCode) {
    this.libraryDesignCode = libraryDesignCode;
  }

  public boolean validate(Library library) {
    if (!(library.getSample() instanceof DetailedSample)) return true;
    if (((DetailedSample) library.getSample()).getSampleClass().getId() != sampleClass.getId()) return false;
    if (library.getLibrarySelectionType().getId() != librarySelectionType.getId()) return false;
    if (library.getLibraryStrategyType().getId() != libraryStrategyType.getId()) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(13, 43)
        .append(librarySelectionType)
        .append(libraryStrategyType)
        .append(name)
        .append(sampleClass)
        .append(libraryDesignCode)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LibraryDesign other = (LibraryDesign) obj;
    return new EqualsBuilder()
        .append(librarySelectionType, other.librarySelectionType)
        .append(libraryStrategyType, other.libraryStrategyType)
        .append(name, other.name)
        .append(sampleClass, other.sampleClass)
        .append(libraryDesignCode, other.libraryDesignCode)
        .isEquals();
  }
}
