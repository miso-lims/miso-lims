package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
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

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class LibraryTemplate implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long libraryTemplateId = UNSAVED_ID;

  String alias;

  @ManyToOne(targetEntity = ProjectImpl.class)
  @JoinColumn(name = "projectId", nullable = false)
  private Project project;

  private Double defaultVolume;

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

  public long getId() {
    return libraryTemplateId;
  }

  public void setId(long libraryTemplateId) {
    this.libraryTemplateId = libraryTemplateId;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public Double getDefaultVolume() {
    return defaultVolume;
  }

  public void setDefaultVolume(Double defaultVolume) {
    this.defaultVolume = defaultVolume;
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
    return indexOnes;
  }

  public void setIndexOnes(Map<String, Index> indexOnes) {
    this.indexOnes = indexOnes;
  }

  public Map<String, Index> getIndexTwos() {
    return indexTwos;
  }

  public void setIndexTwos(Map<String, Index> indexTwos) {
    this.indexTwos = indexTwos;
  }

}
