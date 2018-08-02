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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Immutable
@Table(name = "PoolableElementView")
public class PoolableElementView implements Serializable, Comparable<PoolableElementView> {

  private static final long serialVersionUID = 1L;

  @Id
  private long dilutionId = LibraryDilution.UNSAVED_ID;

  private String dilutionName;

  private Double dilutionConcentration;

  @Enumerated(EnumType.STRING)
  private ConcentrationUnit dilutionConcentrationUnits;

  private String dilutionBarcode;

  private Double dilutionVolume;

  @Enumerated(EnumType.STRING)
  private VolumeUnit dilutionVolumeUnits;

  private Double dilutionNgUsed;

  private Double dilutionVolumeUsed;

  private Long preMigrationId;

  @Temporal(TemporalType.TIMESTAMP)
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
  
  private Boolean libraryQcPassed;

  private String librarySelectionType;

  private String libraryStrategyType;

  // Note: @LazyToOne used because setting FetchType.LAZY prevents extensions from loading (DetailedSample)
  @ManyToOne(targetEntity = SampleImpl.class)
  @LazyToOne(LazyToOneOption.PROXY)
  @JoinColumn(name = "sampleId", insertable = false, updatable = false)
  private Sample sample;

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

  private Long targetedSequencingId;

  private String boxAlias;

  private String boxName;

  private String boxIdentificationBarcode;

  private String boxLocationBarcode;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @OneToMany(targetEntity = Index.class)
  @JoinTable(name = "Library_Index", joinColumns = {
      @JoinColumn(name = "library_libraryId", nullable = false, referencedColumnName = "libraryId") }, inverseJoinColumns = {
          @JoinColumn(name = "index_indexId", nullable = false) })
  private List<Index> indices = new ArrayList<>();

  public static PoolableElementView fromDilution(LibraryDilution dilution) {
    PoolableElementView v = new PoolableElementView();
    v.setDilutionBarcode(dilution.getIdentificationBarcode());
    v.setDilutionConcentration(dilution.getConcentration());
    v.setDilutionConcentrationUnits(dilution.getConcentrationUnits());
    v.setDilutionNgUsed(dilution.getNgUsed());
    v.setDilutionId(dilution.getId());
    v.setDilutionName(dilution.getName());
    v.setDilutionVolume(dilution.getVolume());
    v.setDilutionVolumeUsed(dilution.getVolumeUsed());
    v.setLastModified(dilution.getLastModified());
    v.setPreMigrationId(dilution.getPreMigrationId());
    if (dilution.getBox() != null) {
      v.setBoxName(dilution.getBox().getName());
      v.setBoxAlias(dilution.getBox().getAlias());
      v.setBoxIdentificationBarcode(dilution.getBox().getIdentificationBarcode());
      v.setBoxLocationBarcode(dilution.getBox().getLocationBarcode());
    }
    
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

  public ConcentrationUnit getDilutionConcentrationUnits() {
    return dilutionConcentrationUnits;
  }

  public void setDilutionConcentrationUnits(ConcentrationUnit dilutionConcentrationUnits) {
    this.dilutionConcentrationUnits = dilutionConcentrationUnits;
  }

  public Double getDilutionNgUsed() {
    return dilutionNgUsed;
  }

  public void setDilutionNgUsed(Double dilutionNmUsed) {
    this.dilutionNgUsed = dilutionNmUsed;
  }

  public Double getDilutionVolumeUsed() {
    return dilutionVolumeUsed;
  }

  public void setDilutionVolumeUsed(Double dilutionVolumeUsed) {
    this.dilutionVolumeUsed = dilutionVolumeUsed;
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

  public Long getTargetedSequencingId() {
    return targetedSequencingId;
  }

  public void setTargetedSequencingId(Long targetedSequencingId) {
    this.targetedSequencingId = targetedSequencingId;
  }

  public Double getDilutionVolume() {
    return dilutionVolume;
  }

  public void setDilutionVolume(Double dilutionVolume) {
    this.dilutionVolume = dilutionVolume;
  }

  public VolumeUnit getDilutionVolumeUnits() {
    return dilutionVolumeUnits;
  }

  public void setDilutionVolumeUnits(VolumeUnit dilutionVolumeUnits) {
    this.dilutionVolumeUnits = dilutionVolumeUnits;
  }

  public String getBoxAlias() {
    return boxAlias;
  }

  public void setBoxAlias(String boxAlias) {
    this.boxAlias = boxAlias;
  }

  public String getBoxName() {
    return boxName;
  }

  public void setBoxName(String boxName) {
    this.boxName = boxName;
  }

  public String getBoxIdentificationBarcode() {
    return boxIdentificationBarcode;
  }

  public void setBoxIdentificationBarcode(String boxIdentificationBarcode) {
    this.boxIdentificationBarcode = boxIdentificationBarcode;
  }

  public String getBoxLocationBarcode() {
    return boxLocationBarcode;
  }

  public void setBoxLocationBarcode(String boxLocationBarcode) {
    this.boxLocationBarcode = boxLocationBarcode;
  }

  /**
   * Note: this field is lazy-loaded and retrieval can impact performance
   * 
   * @return the dilution's Sample parent
   */
  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public Boolean getLibraryQcPassed() {
    return libraryQcPassed;
  }

  public void setLibraryQcPassed(Boolean libraryQcPassed) {
    this.libraryQcPassed = libraryQcPassed;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((boxAlias == null) ? 0 : boxAlias.hashCode());
    result = prime * result + ((boxIdentificationBarcode == null) ? 0 : boxIdentificationBarcode.hashCode());
    result = prime * result + ((boxLocationBarcode == null) ? 0 : boxLocationBarcode.hashCode());
    result = prime * result + ((boxName == null) ? 0 : boxName.hashCode());
    result = prime * result + ((created == null) ? 0 : created.hashCode());
    result = prime * result + ((creatorName == null) ? 0 : creatorName.hashCode());
    result = prime * result + ((dilutionBarcode == null) ? 0 : dilutionBarcode.hashCode());
    result = prime * result + ((dilutionConcentration == null) ? 0 : dilutionConcentration.hashCode());
    result = prime * result + ((dilutionConcentrationUnits == null) ? 0 : dilutionConcentrationUnits.hashCode());
    result = prime * result + (int) (dilutionId ^ (dilutionId >>> 32));
    result = prime * result + ((dilutionName == null) ? 0 : dilutionName.hashCode());
    result = prime * result + ((dilutionNgUsed == null) ? 0 : dilutionNgUsed.hashCode());
    result = prime * result + ((dilutionVolume == null) ? 0 : dilutionVolume.hashCode());
    result = prime * result + ((dilutionVolumeUsed == null) ? 0 : dilutionVolumeUsed.hashCode());
    result = prime * result + ((indices == null) ? 0 : indices.hashCode());
    result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
    result = prime * result + ((lastModifierName == null) ? 0 : lastModifierName.hashCode());
    result = prime * result + ((libraryAlias == null) ? 0 : libraryAlias.hashCode());
    result = prime * result + ((libraryBarcode == null) ? 0 : libraryBarcode.hashCode());
    result = prime * result + ((libraryDescription == null) ? 0 : libraryDescription.hashCode());
    result = prime * result + ((libraryDnaSize == null) ? 0 : libraryDnaSize.hashCode());
    result = prime * result + ((libraryId == null) ? 0 : libraryId.hashCode());
    result = prime * result + ((libraryName == null) ? 0 : libraryName.hashCode());
    result = prime * result + (libraryPaired ? 1231 : 1237);
    result = prime * result + ((librarySelectionType == null) ? 0 : librarySelectionType.hashCode());
    result = prime * result + ((libraryStrategyType == null) ? 0 : libraryStrategyType.hashCode());
    result = prime * result + (lowQualityLibrary ? 1231 : 1237);
    result = prime * result + ((platformType == null) ? 0 : platformType.hashCode());
    result = prime * result + ((preMigrationId == null) ? 0 : preMigrationId.hashCode());
    result = prime * result + ((projectAlias == null) ? 0 : projectAlias.hashCode());
    result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
    result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
    result = prime * result + ((projectShortName == null) ? 0 : projectShortName.hashCode());
    result = prime * result + ((sampleAccession == null) ? 0 : sampleAccession.hashCode());
    result = prime * result + ((sampleAlias == null) ? 0 : sampleAlias.hashCode());
    result = prime * result + ((sampleDescription == null) ? 0 : sampleDescription.hashCode());
    result = prime * result + ((sampleId == null) ? 0 : sampleId.hashCode());
    result = prime * result + ((sampleName == null) ? 0 : sampleName.hashCode());
    result = prime * result + ((sampleType == null) ? 0 : sampleType.hashCode());
    result = prime * result + ((targetedSequencingId == null) ? 0 : targetedSequencingId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PoolableElementView other = (PoolableElementView) obj;
    if (boxAlias == null) {
      if (other.boxAlias != null) return false;
    } else if (!boxAlias.equals(other.boxAlias)) return false;
    if (boxIdentificationBarcode == null) {
      if (other.boxIdentificationBarcode != null) return false;
    } else if (!boxIdentificationBarcode.equals(other.boxIdentificationBarcode)) return false;
    if (boxLocationBarcode == null) {
      if (other.boxLocationBarcode != null) return false;
    } else if (!boxLocationBarcode.equals(other.boxLocationBarcode)) return false;
    if (boxName == null) {
      if (other.boxName != null) return false;
    } else if (!boxName.equals(other.boxName)) return false;
    if (created == null) {
      if (other.created != null) return false;
    } else if (!created.equals(other.created)) return false;
    if (creatorName == null) {
      if (other.creatorName != null) return false;
    } else if (!creatorName.equals(other.creatorName)) return false;
    if (dilutionBarcode == null) {
      if (other.dilutionBarcode != null) return false;
    } else if (!dilutionBarcode.equals(other.dilutionBarcode)) return false;
    if (dilutionConcentration == null) {
      if (other.dilutionConcentration != null) return false;
    } else if (!dilutionConcentration.equals(other.dilutionConcentration)) return false;
    if (dilutionConcentrationUnits == null) {
      if (other.dilutionConcentrationUnits != null) return false;
    } else if (!dilutionConcentrationUnits.equals(other.dilutionConcentrationUnits)) return false;
    if (dilutionId != other.dilutionId) return false;
    if (dilutionName == null) {
      if (other.dilutionName != null) return false;
    } else if (!dilutionName.equals(other.dilutionName)) return false;
    if (dilutionNgUsed == null) {
      if (other.dilutionNgUsed != null) return false;
    } else if (!dilutionNgUsed.equals(other.dilutionNgUsed)) return false;
    if (dilutionVolume == null) {
      if (other.dilutionVolume != null) return false;
    } else if (!dilutionVolume.equals(other.dilutionVolume)) return false;
    if (dilutionVolumeUsed == null) {
      if (other.dilutionVolumeUsed != null) return false;
    } else if (!dilutionVolumeUsed.equals(other.dilutionVolumeUsed)) return false;
    if (indices == null) {
      if (other.indices != null) return false;
    } else if (!indices.equals(other.indices)) return false;
    if (lastModified == null) {
      if (other.lastModified != null) return false;
    } else if (!lastModified.equals(other.lastModified)) return false;
    if (lastModifierName == null) {
      if (other.lastModifierName != null) return false;
    } else if (!lastModifierName.equals(other.lastModifierName)) return false;
    if (libraryAlias == null) {
      if (other.libraryAlias != null) return false;
    } else if (!libraryAlias.equals(other.libraryAlias)) return false;
    if (libraryBarcode == null) {
      if (other.libraryBarcode != null) return false;
    } else if (!libraryBarcode.equals(other.libraryBarcode)) return false;
    if (libraryDescription == null) {
      if (other.libraryDescription != null) return false;
    } else if (!libraryDescription.equals(other.libraryDescription)) return false;
    if (libraryDnaSize == null) {
      if (other.libraryDnaSize != null) return false;
    } else if (!libraryDnaSize.equals(other.libraryDnaSize)) return false;
    if (libraryId == null) {
      if (other.libraryId != null) return false;
    } else if (!libraryId.equals(other.libraryId)) return false;
    if (libraryName == null) {
      if (other.libraryName != null) return false;
    } else if (!libraryName.equals(other.libraryName)) return false;
    if (libraryPaired != other.libraryPaired) return false;
    if (librarySelectionType == null) {
      if (other.librarySelectionType != null) return false;
    } else if (!librarySelectionType.equals(other.librarySelectionType)) return false;
    if (libraryStrategyType == null) {
      if (other.libraryStrategyType != null) return false;
    } else if (!libraryStrategyType.equals(other.libraryStrategyType)) return false;
    if (lowQualityLibrary != other.lowQualityLibrary) return false;
    if (platformType != other.platformType) return false;
    if (preMigrationId == null) {
      if (other.preMigrationId != null) return false;
    } else if (!preMigrationId.equals(other.preMigrationId)) return false;
    if (projectAlias == null) {
      if (other.projectAlias != null) return false;
    } else if (!projectAlias.equals(other.projectAlias)) return false;
    if (projectId == null) {
      if (other.projectId != null) return false;
    } else if (!projectId.equals(other.projectId)) return false;
    if (projectName == null) {
      if (other.projectName != null) return false;
    } else if (!projectName.equals(other.projectName)) return false;
    if (projectShortName == null) {
      if (other.projectShortName != null) return false;
    } else if (!projectShortName.equals(other.projectShortName)) return false;
    if (sampleAccession == null) {
      if (other.sampleAccession != null) return false;
    } else if (!sampleAccession.equals(other.sampleAccession)) return false;
    if (sampleAlias == null) {
      if (other.sampleAlias != null) return false;
    } else if (!sampleAlias.equals(other.sampleAlias)) return false;
    if (sampleDescription == null) {
      if (other.sampleDescription != null) return false;
    } else if (!sampleDescription.equals(other.sampleDescription)) return false;
    if (sampleId == null) {
      if (other.sampleId != null) return false;
    } else if (!sampleId.equals(other.sampleId)) return false;
    if (sampleName == null) {
      if (other.sampleName != null) return false;
    } else if (!sampleName.equals(other.sampleName)) return false;
    if (sampleType == null) {
      if (other.sampleType != null) return false;
    } else if (!sampleType.equals(other.sampleType)) return false;
    if (targetedSequencingId == null) {
      if (other.targetedSequencingId != null) return false;
    } else if (!targetedSequencingId.equals(other.targetedSequencingId)) return false;
    return true;
  }

}
