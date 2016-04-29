package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "LibraryPropagationRule")
public class LibraryPropagationRule {
  @Id
  private Long libraryPropagationRuleId;
  @Column(nullable = false)
  private String name;
  @OneToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "sampleClassId")
  private SampleClass sampleClass;
  @Enumerated(EnumType.STRING)
  private PlatformType platformName;
  private Boolean paired;
  private Long librarySelectionType;
  private Long libraryStrategyType;

  public Long getId() {
    return libraryPropagationRuleId;
  }

  public void setId(Long libraryPropagationRuleId) {
    this.libraryPropagationRuleId = libraryPropagationRuleId;
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

  public PlatformType getPlatform() {
    return platformName;
  }

  public void setPlatform(PlatformType platformName) {
    this.platformName = platformName;
  }

  public Boolean getPaired() {
    return paired;
  }

  public void setPaired(Boolean paired) {
    this.paired = paired;
  }

  public Long getLibrarySelectionType() {
    return librarySelectionType;
  }

  public void setLibrarySelectionType(Long librarySelectionType) {
    this.librarySelectionType = librarySelectionType;
  }

  public Long getLibraryStrategyType() {
    return libraryStrategyType;
  }

  public void setLibraryStrategyType(Long libraryStrategyType) {
    this.libraryStrategyType = libraryStrategyType;
  }

  public boolean validate(Library library) {
    if (library.getSample().getSampleAdditionalInfo() == null) return true;
    if (sampleClass != null
        && library.getSample().getSampleAdditionalInfo().getSampleClass().getSampleClassId() != sampleClass.getSampleClassId())
      return false;
    if (platformName != null && !library.getPlatformName().equals(platformName.getKey())) return false;
    if (paired != null && library.getPaired().booleanValue() != paired.booleanValue()) return false;
    if (librarySelectionType != null && library.getLibrarySelectionType().getLibrarySelectionTypeId() != librarySelectionType) return false;
    if (libraryStrategyType != null && library.getLibraryStrategyType().getLibraryStrategyTypeId() != libraryStrategyType) return false;
    return true;
  }

  public static boolean validate(Library library, Iterable<LibraryPropagationRule> rules) {
    // Return true if the ruleset is empty.
    boolean first = true;
    for (LibraryPropagationRule rule : rules) {
      if (rule.validate(library)) return true;
      first = false;
    }
    return first;
  }

  /**
   * Check if this rule can be used as a library design.
   * 
   * A library design is a rule that has no wildcards in it. So, if any selection strategy is allowed (indicated by that property being
   * null), they it isn't a design. As long as everything is locked, a rule is a design.
   */
  public boolean isDesign() {
    return name != null && !name.isEmpty() && platformName != null && paired != null && librarySelectionType != null
        && libraryStrategyType != null;
  }
}
