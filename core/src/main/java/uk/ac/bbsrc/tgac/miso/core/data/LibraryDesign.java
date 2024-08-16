package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;

@Entity
@Table(name = "LibraryDesign")
public class LibraryDesign implements Deletable, Serializable {

  public static boolean validate(Library library, Iterable<LibraryDesign> rules) {
    // Return true if the ruleset is empty.
    boolean first = true;
    for (LibraryDesign rule : rules) {
      if (rule.validate(library))
        return true;
      first = false;
    }
    return first;
  }

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long libraryDesignId = UNSAVED_ID;

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

  @Override
  public long getId() {
    return libraryDesignId;
  }

  @Override
  public void setId(long libraryDesignId) {
    this.libraryDesignId = libraryDesignId;
  }

  public LibrarySelectionType getLibrarySelectionType() {
    return librarySelectionType;
  }

  public void setLibrarySelectionType(LibrarySelectionType librarySelectionType) {
    this.librarySelectionType = librarySelectionType;
  }

  public LibraryStrategyType getLibraryStrategyType() {
    return libraryStrategyType;
  }

  public void setLibraryStrategyType(LibraryStrategyType libraryStrategyType) {
    this.libraryStrategyType = libraryStrategyType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SampleClass getSampleClass() {
    return sampleClass;
  }

  public void setSampleClass(SampleClass sampleClass) {
    this.sampleClass = sampleClass;
  }

  public LibraryDesignCode getLibraryDesignCode() {
    return libraryDesignCode;
  }

  public void setLibraryDesignCode(LibraryDesignCode libraryDesignCode) {
    this.libraryDesignCode = libraryDesignCode;
  }

  public boolean validate(Library library) {
    if (!(library.getSample() instanceof DetailedSample))
      return true;
    if (((DetailedSample) library.getSample()).getSampleClass().getId() != sampleClass.getId())
      return false;
    if (library.getLibrarySelectionType().getId() != librarySelectionType.getId())
      return false;
    if (library.getLibraryStrategyType().getId() != libraryStrategyType.getId())
      return false;
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LibraryDesign other = (LibraryDesign) obj;
    return new EqualsBuilder()
        .append(librarySelectionType, other.librarySelectionType)
        .append(libraryStrategyType, other.libraryStrategyType)
        .append(name, other.name)
        .append(sampleClass, other.sampleClass)
        .append(libraryDesignCode, other.libraryDesignCode)
        .isEquals();
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Library Design";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }
}
