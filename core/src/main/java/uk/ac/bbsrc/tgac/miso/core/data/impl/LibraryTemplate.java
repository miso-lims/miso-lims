package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class LibraryTemplate implements Serializable, Deletable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long libraryTemplateId = UNSAVED_ID;

  String alias;

  @ManyToMany(targetEntity = ProjectImpl.class)
  @JoinTable(name = "LibraryTemplate_Project", joinColumns = {
      @JoinColumn(name = "libraryTemplateId", nullable = false) }, inverseJoinColumns = {
          @JoinColumn(name = "projectId", nullable = false) })
  private List<Project> projects = new ArrayList<>();

  private BigDecimal defaultVolume;

  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @ManyToOne
  @JoinColumn(name = "libraryTypeId")
  private LibraryType libraryType;

  @ManyToOne
  @JoinColumn(name = "librarySelectionTypeId")
  private LibrarySelectionType librarySelectionType;

  @ManyToOne
  @JoinColumn(name = "libraryStrategyTypeId")
  private LibraryStrategyType libraryStrategyType;

  @ManyToOne
  @JoinColumn(name = "kitDescriptorId")
  private KitDescriptor kitDescriptor;

  @ManyToOne
  @JoinColumn(name = "indexFamilyId")
  private IndexFamily indexFamily;

  @ManyToMany(targetEntity = Index.class)
  @MapKeyColumn(name = "position", unique = true)
  @JoinTable(name = "LibraryTemplate_Index1", joinColumns = { @JoinColumn(name = "libraryTemplateId") }, inverseJoinColumns = {
      @JoinColumn(name = "indexId") })
  private Map<String, Index> indexOnes;

  @ManyToMany(targetEntity = Index.class)
  @MapKeyColumn(name = "position", unique = true)
  @JoinTable(name = "LibraryTemplate_Index2", joinColumns = { @JoinColumn(name = "libraryTemplateId") }, inverseJoinColumns = {
      @JoinColumn(name = "indexId") })
  private Map<String, Index> indexTwos;

  @Override
  public long getId() {
    return libraryTemplateId;
  }

  @Override
  public void setId(long libraryTemplateId) {
    this.libraryTemplateId = libraryTemplateId;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public List<Project> getProjects() {
    if (projects == null) {
      projects = new ArrayList<>();
    }
    return projects;
  }

  public BigDecimal getDefaultVolume() {
    return defaultVolume;
  }

  public void setDefaultVolume(BigDecimal defaultVolume) {
    this.defaultVolume = defaultVolume;
  }

  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public LibraryType getLibraryType() {
    return libraryType;
  }

  public void setLibraryType(LibraryType libraryType) {
    this.libraryType = libraryType;
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

  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

  public IndexFamily getIndexFamily() {
    return indexFamily;
  }

  public void setIndexFamily(IndexFamily indexFamily) {
    this.indexFamily = indexFamily;
  }

  public Map<String, Index> getIndexOnes() {
    if (indexOnes == null) {
      indexOnes = new HashMap<>();
    }
    return indexOnes;
  }

  public Map<String, Index> getIndexTwos() {
    if (indexTwos == null) {
      indexTwos = new HashMap<>();
    }
    return indexTwos;
  }

  @Override
  public String getDeleteType() {
    return "Library Template";
  }

  @Override
  public String getDeleteDescription() {
    // return getAlias() + " (" + getProjects().getAlias() + ")";
    return getAlias();
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

}
