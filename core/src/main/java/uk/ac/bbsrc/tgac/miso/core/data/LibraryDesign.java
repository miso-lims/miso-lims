package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;

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
  @Column(nullable = false)
  private Long librarySelectionType;
  @Column(nullable = false)
  private Long libraryStrategyType;
  @OneToOne(targetEntity = LibraryType.class)
  @JoinColumn(name = "libraryType", nullable = false)
  private LibraryType libraryType;
  @Column(nullable = false)
  private String name;

  @OneToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "sampleClassId", nullable = false)
  private SampleClass sampleClass;
  @Column(nullable = false)
  private String suffix;

  public Long getId() {
    return libraryDesignId;
  }

  public Long getLibrarySelectionType() {
    return librarySelectionType;
  }

  public Long getLibraryStrategyType() {
    return libraryStrategyType;
  }

  public LibraryType getLibraryType() {
    return libraryType;
  }

  public String getName() {
    return name;
  }

  public SampleClass getSampleClass() {
    return sampleClass;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setId(Long libraryDesignId) {
    this.libraryDesignId = libraryDesignId;
  }

  public void setLibrarySelectionType(Long librarySelectionType) {
    this.librarySelectionType = librarySelectionType;
  }

  public void setLibraryStrategyType(Long libraryStrategyType) {
    this.libraryStrategyType = libraryStrategyType;
  }

  public void setLibraryType(LibraryType libraryType) {
    this.libraryType = libraryType;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSampleClass(SampleClass sampleClass) {
    this.sampleClass = sampleClass;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public boolean validate(Library library) {
    if (!(library.getSample() instanceof DetailedSample)) return true;
    if (((DetailedSample) library.getSample()).getSampleClass().getId() != sampleClass.getId()) return false;
    if (library.getLibraryType().getId() != libraryType.getId()) return false;
    if (library.getLibrarySelectionType().getId() != librarySelectionType) return false;
    if (library.getLibraryStrategyType().getId() != libraryStrategyType) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(13, 43)
        .append(librarySelectionType)
        .append(libraryStrategyType)
        .append(libraryType)
        .append(name)
        .append(sampleClass)
        .append(suffix)
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
        .append(libraryType, other.libraryType)
        .append(name, other.name)
        .append(sampleClass, other.sampleClass)
        .append(suffix, other.suffix)
        .isEquals();
  }
}
