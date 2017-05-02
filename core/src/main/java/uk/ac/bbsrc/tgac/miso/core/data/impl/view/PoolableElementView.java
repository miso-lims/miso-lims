package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "PoolableElementView")
public class PoolableElementView implements Serializable, Comparable<PoolableElementView> {

  private static final long serialVersionUID = 1L;

  @Id
  private long dilutionId = LibraryDilution.UNSAVED_ID;

  private String dilutionName;

  private Double dilutionConcentration;

  private String dilutionBarcode;

  private Long preMigrationId;

  @Temporal(TemporalType.DATE)
  private Date lastModified;

  @Temporal(TemporalType.DATE)
  private Date created;

  private Long libraryId;

  private String libraryName;

  private String libraryAlias;

  private String libraryDescription;

  private String libraryBarcode;

  private Long libraryDnaSize;

  private boolean libraryPaired;

  @Column(nullable = false)
  private boolean lowQualityLibrary = false;

  private String librarySelectionType;

  private String libraryStrategyType;

  private Long sampleId;

  private String sampleName;

  private String sampleAlias;

  private String sampleDescription;

  private String sampleAccession;

  private String sampleType;

  private Long projectId;

  private String projectName;

  private String projectShortName;

  private String projectAlias;

  private String lastModifierName;

  private String creatorName;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @OneToMany(targetEntity = Index.class)
  @JoinTable(name = "Library_Index", joinColumns = {
      @JoinColumn(name = "library_libraryId", nullable = false, referencedColumnName = "libraryId") }, inverseJoinColumns = {
          @JoinColumn(name = "index_indexId", nullable = false) })
  private List<Index> indices = new ArrayList<>();

  public static PoolableElementView fromDilution(Dilution dilution) {
    PoolableElementView v = new PoolableElementView();
    v.setDilutionBarcode(dilution.getIdentificationBarcode());
    v.setDilutionConcentration(dilution.getConcentration());
    v.setDilutionId(dilution.getId());
    v.setDilutionName(dilution.getName());
    v.setLastModified(dilution.getLastModified());
    v.setPreMigrationId(dilution.getPreMigrationId());
    
    Library lib = dilution.getLibrary();
    if (lib != null) {
      v.setIndices(lib.getIndices());
      v.setLibraryAlias(lib.getAlias());
      v.setLibraryBarcode(lib.getIdentificationBarcode());
      v.setLibraryDescription(lib.getDescription());
      if (lib.getDnaSize() != null) {
        v.setLibraryDnaSize(lib.getDnaSize().longValue());
      }
      v.setLibraryId(lib.getId());
      v.setLibraryName(lib.getName());
      v.setLibraryPaired(lib.getPaired());
      if (lib.getLibrarySelectionType() != null) {
        v.setLibrarySelectionType(lib.getLibrarySelectionType().getName());
      }
      if (lib.getLibraryStrategyType() != null) {
        v.setLibraryStrategyType(lib.getLibraryStrategyType().getName());
      }
      v.setLowQualityLibrary(lib.isLowQuality());
      v.setPlatformType(lib.getPlatformType());
      
      Sample sam = lib.getSample();
      if (sam != null) {
        v.setSampleAccession(sam.getAccession());
        v.setSampleAlias(sam.getAlias());
        v.setSampleDescription(sam.getDescription());
        v.setSampleId(sam.getId());
        v.setSampleName(sam.getName());
        v.setSampleType(sam.getSampleType());
        
        Project proj = sam.getProject();
        if (proj != null) {
          v.setProjectAlias(proj.getAlias());
          v.setProjectId(proj.getId());
          v.setProjectName(proj.getName());
          v.setProjectShortName(proj.getShortName());
        }
      }
    }
    return v;
  }

  public List<Index> getIndices() {
    return indices;
  }

  public void setIndices(List<Index> indices) {
    this.indices = indices;
  }

  public long getDilutionId() {
    return dilutionId;
  }

  public void setDilutionId(long dilutionId) {
    this.dilutionId = dilutionId;
  }

  public String getDilutionName() {
    return dilutionName;
  }

  public void setDilutionName(String dilutionName) {
    this.dilutionName = dilutionName;
  }

  public Double getDilutionConcentration() {
    return dilutionConcentration;
  }

  public void setDilutionConcentration(Double dilutionConcentration) {
    this.dilutionConcentration = dilutionConcentration;
  }

  public String getProjectShortName() {
    return projectShortName;
  }

  public void setProjectShortName(String projectShortName) {
    this.projectShortName = projectShortName;
  }

  public String getProjectAlias() {
    return projectAlias;
  }

  public void setProjectAlias(String projectAlias) {
    this.projectAlias = projectAlias;
  }

  public static String getUnits() {
    return LibraryDilution.UNITS;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getDilutionBarcode() {
    return dilutionBarcode;
  }

  public void setDilutionBarcode(String dilutionBarcode) {
    this.dilutionBarcode = dilutionBarcode;
  }

  public Long getPreMigrationId() {
    return preMigrationId;
  }

  public void setPreMigrationId(Long preMigrationId) {
    this.preMigrationId = preMigrationId;
  }

  public Long getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }

  public String getLibraryName() {
    return libraryName;
  }

  public void setLibraryName(String libraryName) {
    this.libraryName = libraryName;
  }

  public String getLibraryAlias() {
    return libraryAlias;
  }

  public void setLibraryAlias(String libraryAlias) {
    this.libraryAlias = libraryAlias;
  }

  public String getLibraryDescription() {
    return libraryDescription;
  }

  public void setLibraryDescription(String libraryDescription) {
    this.libraryDescription = libraryDescription;
  }

  public String getLibraryBarcode() {
    return libraryBarcode;
  }

  public void setLibraryBarcode(String libraryBarcode) {
    this.libraryBarcode = libraryBarcode;
  }

  public Long getLibraryDnaSize() {
    return libraryDnaSize;
  }

  public void setLibraryDnaSize(Long libraryDnaSize) {
    this.libraryDnaSize = libraryDnaSize;
  }

  public boolean isLibraryPaired() {
    return libraryPaired;
  }

  public void setLibraryPaired(boolean libraryPaired) {
    this.libraryPaired = libraryPaired;
  }

  public boolean isLowQualityLibrary() {
    return lowQualityLibrary;
  }

  public void setLowQualityLibrary(boolean lowQualityLibrary) {
    this.lowQualityLibrary = lowQualityLibrary;
  }

  public String getLibrarySelectionType() {
    return librarySelectionType;
  }

  public void setLibrarySelectionType(String librarySelectionType) {
    this.librarySelectionType = librarySelectionType;
  }

  public String getLibraryStrategyType() {
    return libraryStrategyType;
  }

  public void setLibraryStrategyType(String libraryStrategyType) {
    this.libraryStrategyType = libraryStrategyType;
  }

  public Long getSampleId() {
    return sampleId;
  }

  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
  }

  public String getSampleName() {
    return sampleName;
  }

  public void setSampleName(String sampleName) {
    this.sampleName = sampleName;
  }

  public String getSampleAlias() {
    return sampleAlias;
  }

  public void setSampleAlias(String sampleAlias) {
    this.sampleAlias = sampleAlias;
  }

  public String getSampleDescription() {
    return sampleDescription;
  }

  public void setSampleDescription(String sampleDescription) {
    this.sampleDescription = sampleDescription;
  }

  public String getSampleAccession() {
    return sampleAccession;
  }

  public void setSampleAccession(String sampleAccession) {
    this.sampleAccession = sampleAccession;
  }

  public String getSampleType() {
    return sampleType;
  }

  public void setSampleType(String sampleType) {
    this.sampleType = sampleType;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getLastModifierName() {
    return lastModifierName;
  }

  public void setLastModifierName(String lastModifierName) {
    this.lastModifierName = lastModifierName;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  @Override
  public int compareTo(PoolableElementView o) {
    PoolableElementView t = o;
    if (getDilutionId() < t.getDilutionId()) return -1;
    if (getDilutionId() > t.getDilutionId()) return 1;
    return 0;
  }

}
