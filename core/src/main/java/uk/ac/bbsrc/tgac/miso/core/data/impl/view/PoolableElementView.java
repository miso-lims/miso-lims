package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingControlType;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.qc.DetailedQcItem;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Immutable
@Table(name = "PoolableElementView")
public class PoolableElementView implements DetailedQcItem, Identifiable, Serializable, Comparable<PoolableElementView> {

  private static final long serialVersionUID = 1L;

  @Id
  private long aliquotId = LibraryAliquot.UNSAVED_ID;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aliquotId")
  private LibraryAliquot aliquot;

  private String aliquotName;
  private String aliquotAlias;
  private Integer aliquotDnaSize;
  private BigDecimal aliquotConcentration;

  @Enumerated(EnumType.STRING)
  private ConcentrationUnit aliquotConcentrationUnits;

  private String aliquotBarcode;
  private BigDecimal aliquotVolume;

  @Enumerated(EnumType.STRING)
  private VolumeUnit aliquotVolumeUnits;

  private BigDecimal aliquotNgUsed;
  private BigDecimal aliquotVolumeUsed;
  private boolean discarded;
  private boolean distributed;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;
  private String detailedQcStatusNote;

  @ManyToOne
  @JoinColumn(name = "libraryDesignCodeId")
  private LibraryDesignCode aliquotDesignCode;

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
  private boolean libraryPaired;
  private boolean libraryLowQuality;
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

  @ManyToOne
  @JoinColumn(name = "sampleSequencingControlTypeId")
  private SequencingControlType sampleSequencingControlType;

  private Long projectId;
  private String projectName;
  private String projectShortName;
  private String projectAlias;
  private Long subprojectId;
  private String subprojectAlias;
  private Boolean subprojectPriority;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  private Long targetedSequencingId;
  private String boxAlias;
  private String boxName;
  private String boxIdentificationBarcode;
  private String boxLocationBarcode;
  private String boxPosition;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @OneToMany(targetEntity = Index.class)
  @JoinTable(name = "Library_Index", joinColumns = {
      @JoinColumn(name = "library_libraryId", nullable = false, referencedColumnName = "libraryId") }, inverseJoinColumns = {
          @JoinColumn(name = "index_indexId", nullable = false) })
  private List<Index> indices = new ArrayList<>();

  @OneToMany(mappedBy = "item")
  private List<TransferLibraryAliquot> transfers;

  public static PoolableElementView fromLibraryAliquot(LibraryAliquot aliquot) {
    PoolableElementView v = new PoolableElementView();
    v.setAliquotBarcode(aliquot.getIdentificationBarcode());
    v.setAliquotConcentration(aliquot.getConcentration());
    v.setAliquotConcentrationUnits(aliquot.getConcentrationUnits());
    v.setAliquotNgUsed(aliquot.getNgUsed());
    v.setAliquotId(aliquot.getId());
    v.setAliquotName(aliquot.getName());
    v.setAliquotVolume(aliquot.getVolume());
    v.setAliquotVolumeUsed(aliquot.getVolumeUsed());
    v.setLastModified(aliquot.getLastModified());
    v.setPreMigrationId(aliquot.getPreMigrationId());
    v.setAliquotDnaSize(aliquot.getDnaSize());
    if (aliquot.getBox() != null) {
      v.setBoxName(aliquot.getBox().getName());
      v.setBoxAlias(aliquot.getBox().getAlias());
      v.setBoxIdentificationBarcode(aliquot.getBox().getIdentificationBarcode());
      v.setBoxLocationBarcode(aliquot.getBox().getLocationBarcode());
    }
    
    Library lib = aliquot.getLibrary();
    if (lib != null) {
      v.setIndices(new ArrayList<>(lib.getIndices()));
      v.setLibraryAlias(lib.getAlias());
      v.setLibraryBarcode(lib.getIdentificationBarcode());
      v.setLibraryDescription(lib.getDescription());
      v.setLibraryId(lib.getId());
      v.setLibraryName(lib.getName());
      v.setLibraryPaired(lib.getPaired());
      if (lib.getLibrarySelectionType() != null) {
        v.setLibrarySelectionType(lib.getLibrarySelectionType().getName());
      }
      if (lib.getLibraryStrategyType() != null) {
        v.setLibraryStrategyType(lib.getLibraryStrategyType().getName());
      }
      v.setLibraryLowQuality(lib.isLowQuality());
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

  public long getAliquotId() {
    return aliquotId;
  }

  public void setAliquotId(long aliquotId) {
    this.aliquotId = aliquotId;
  }

  public LibraryAliquot getAliquot() {
    return aliquot;
  }

  public void setAliquot(LibraryAliquot aliquot) {
    this.aliquot = aliquot;
  }

  public String getAliquotName() {
    return aliquotName;
  }

  public void setAliquotName(String aliquotName) {
    this.aliquotName = aliquotName;
  }

  public String getAliquotAlias() {
    return aliquotAlias;
  }

  public void setAliquotAlias(String aliquotAlias) {
    this.aliquotAlias = aliquotAlias;
  }

  public Integer getAliquotDnaSize() {
    return aliquotDnaSize;
  }

  public void setAliquotDnaSize(Integer aliquotDnaSize) {
    this.aliquotDnaSize = aliquotDnaSize;
  }

  public BigDecimal getAliquotConcentration() {
    return aliquotConcentration;
  }

  public void setAliquotConcentration(BigDecimal aliquotConcentration) {
    this.aliquotConcentration = aliquotConcentration;
  }

  public ConcentrationUnit getAliquotConcentrationUnits() {
    return aliquotConcentrationUnits;
  }

  public void setAliquotConcentrationUnits(ConcentrationUnit aliquotConcentrationUnits) {
    this.aliquotConcentrationUnits = aliquotConcentrationUnits;
  }

  public BigDecimal getAliquotNgUsed() {
    return aliquotNgUsed;
  }

  public void setAliquotNgUsed(BigDecimal aliquotNmUsed) {
    this.aliquotNgUsed = aliquotNmUsed;
  }

  public BigDecimal getAliquotVolumeUsed() {
    return aliquotVolumeUsed;
  }

  public void setAliquotVolumeUsed(BigDecimal aliquotVolumeUsed) {
    this.aliquotVolumeUsed = aliquotVolumeUsed;
  }

  @Override
  public DetailedQcStatus getDetailedQcStatus() {
    return detailedQcStatus;
  }

  @Override
  public void setDetailedQcStatus(DetailedQcStatus detailedQcStatus) {
    this.detailedQcStatus = detailedQcStatus;
  }

  @Override
  public String getDetailedQcStatusNote() {
    return detailedQcStatusNote;
  }

  @Override
  public void setDetailedQcStatusNote(String detailedQcStatusNote) {
    this.detailedQcStatusNote = detailedQcStatusNote;
  }

  public LibraryDesignCode getAliquotDesignCode() {
    return aliquotDesignCode;
  }

  public void setAliquotDesignCode(LibraryDesignCode aliquotDesignCode) {
    this.aliquotDesignCode = aliquotDesignCode;
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

  public Long getSubprojectId() {
    return subprojectId;
  }

  public void setSubprojectId(Long subprojectId) {
    this.subprojectId = subprojectId;
  }

  public String getSubprojectAlias() {
    return subprojectAlias;
  }

  public void setSubprojectAlias(String subprojectAlias) {
    this.subprojectAlias = subprojectAlias;
  }

  public Boolean getSubprojectPriority() {
    return subprojectPriority;
  }

  public void setSubprojectPriority(Boolean subprojectPriority) {
    this.subprojectPriority = subprojectPriority;
  }

  public String getAliquotBarcode() {
    return aliquotBarcode;
  }

  public void setAliquotBarcode(String aliquotBarcode) {
    this.aliquotBarcode = aliquotBarcode;
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

  public boolean isLibraryPaired() {
    return libraryPaired;
  }

  public void setLibraryPaired(boolean libraryPaired) {
    this.libraryPaired = libraryPaired;
  }

  public boolean isLibraryLowQuality() {
    return libraryLowQuality;
  }

  public void setLibraryLowQuality(boolean libraryLowQuality) {
    this.libraryLowQuality = libraryLowQuality;
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

  public SequencingControlType getSampleSequencingControlType() {
    return sampleSequencingControlType;
  }

  public void setSampleSequencingControlType(SequencingControlType sampleSequencingControlType) {
    this.sampleSequencingControlType = sampleSequencingControlType;
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

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public int compareTo(PoolableElementView o) {
    PoolableElementView t = o;
    if (getAliquotId() < t.getAliquotId()) return -1;
    if (getAliquotId() > t.getAliquotId()) return 1;
    return 0;
  }

  public Long getTargetedSequencingId() {
    return targetedSequencingId;
  }

  public void setTargetedSequencingId(Long targetedSequencingId) {
    this.targetedSequencingId = targetedSequencingId;
  }

  public BigDecimal getAliquotVolume() {
    return aliquotVolume;
  }

  public void setAliquotVolume(BigDecimal aliquotVolume) {
    this.aliquotVolume = aliquotVolume;
  }

  public VolumeUnit getAliquotVolumeUnits() {
    return aliquotVolumeUnits;
  }

  public void setAliquotVolumeUnits(VolumeUnit aliquotVolumeUnits) {
    this.aliquotVolumeUnits = aliquotVolumeUnits;
  }

  public boolean isDiscarded() {
    return discarded;
  }

  public void setDiscarded(boolean discarded) {
    this.discarded = discarded;
  }

  public boolean isDistributed() {
    return distributed;
  }

  public void setDistributed(boolean distributed) {
    this.distributed = distributed;
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

  public String getBoxPosition() {
    return boxPosition;
  }

  public void setBoxPosition(String boxPosition) {
    this.boxPosition = boxPosition;
  }

  /**
   * Note: this field is lazy-loaded and retrieval can impact performance
   * 
   * @return the aliquot's Sample parent
   */
  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public List<TransferLibraryAliquot> getTransfers() {
    if (transfers == null) {
      transfers = new ArrayList<>();
    }
    return transfers;
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
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + ((aliquotBarcode == null) ? 0 : aliquotBarcode.hashCode());
    result = prime * result + ((aliquotDnaSize == null) ? 0 : aliquotDnaSize.hashCode());
    result = prime * result + ((aliquotConcentration == null) ? 0 : aliquotConcentration.hashCode());
    result = prime * result + ((aliquotConcentrationUnits == null) ? 0 : aliquotConcentrationUnits.hashCode());
    result = prime * result + (int) (aliquotId ^ (aliquotId >>> 32));
    result = prime * result + ((aliquotName == null) ? 0 : aliquotName.hashCode());
    result = prime * result + ((aliquotNgUsed == null) ? 0 : aliquotNgUsed.hashCode());
    result = prime * result + ((aliquotVolume == null) ? 0 : aliquotVolume.hashCode());
    result = prime * result + ((aliquotVolumeUsed == null) ? 0 : aliquotVolumeUsed.hashCode());
    result = prime * result + ((detailedQcStatus == null) ? 0 : detailedQcStatus.hashCode());
    result = prime * result + ((detailedQcStatusNote == null) ? 0 : detailedQcStatusNote.hashCode());
    result = prime * result + ((indices == null) ? 0 : indices.hashCode());
    result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
    result = prime * result + ((lastModifier == null) ? 0 : lastModifier.hashCode());
    result = prime * result + ((libraryAlias == null) ? 0 : libraryAlias.hashCode());
    result = prime * result + ((libraryBarcode == null) ? 0 : libraryBarcode.hashCode());
    result = prime * result + ((libraryDescription == null) ? 0 : libraryDescription.hashCode());
    result = prime * result + ((libraryId == null) ? 0 : libraryId.hashCode());
    result = prime * result + ((libraryName == null) ? 0 : libraryName.hashCode());
    result = prime * result + (libraryPaired ? 1231 : 1237);
    result = prime * result + ((librarySelectionType == null) ? 0 : librarySelectionType.hashCode());
    result = prime * result + ((libraryStrategyType == null) ? 0 : libraryStrategyType.hashCode());
    result = prime * result + (libraryLowQuality ? 1231 : 1237);
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
    if (creator == null) {
      if (other.creator != null) return false;
    } else if (!creator.equals(other.creator)) return false;
    if (aliquotBarcode == null) {
      if (other.aliquotBarcode != null) return false;
    } else if (!aliquotBarcode.equals(other.aliquotBarcode)) return false;
    if (aliquotDnaSize == null) {
      if (other.aliquotDnaSize != null) return false;
    } else if (!aliquotDnaSize.equals(other.aliquotDnaSize)) return false;
    if (aliquotConcentration == null) {
      if (other.aliquotConcentration != null) return false;
    } else if (!aliquotConcentration.equals(other.aliquotConcentration)) return false;
    if (aliquotConcentrationUnits == null) {
      if (other.aliquotConcentrationUnits != null) return false;
    } else if (!aliquotConcentrationUnits.equals(other.aliquotConcentrationUnits)) return false;
    if (aliquotId != other.aliquotId) return false;
    if (aliquotName == null) {
      if (other.aliquotName != null) return false;
    } else if (!aliquotName.equals(other.aliquotName)) return false;
    if (aliquotNgUsed == null) {
      if (other.aliquotNgUsed != null) return false;
    } else if (!aliquotNgUsed.equals(other.aliquotNgUsed)) return false;
    if (aliquotVolume == null) {
      if (other.aliquotVolume != null) return false;
    } else if (!aliquotVolume.equals(other.aliquotVolume)) return false;
    if (aliquotVolumeUsed == null) {
      if (other.aliquotVolumeUsed != null) return false;
    } else if (!aliquotVolumeUsed.equals(other.aliquotVolumeUsed)) return false;
    if (detailedQcStatus == null) {
      if (other.detailedQcStatus != null) return false;
    } else if (!detailedQcStatus.equals(other.detailedQcStatus)) return false;
    if (detailedQcStatusNote == null) {
      if (other.detailedQcStatusNote != null) return false;
    } else if (!detailedQcStatusNote.equals(other.detailedQcStatusNote)) return false;
    if (indices == null) {
      if (other.indices != null) return false;
    } else if (!indices.equals(other.indices)) return false;
    if (lastModified == null) {
      if (other.lastModified != null) return false;
    } else if (!lastModified.equals(other.lastModified)) return false;
    if (lastModifier == null) {
      if (other.lastModifier != null) return false;
    } else if (!lastModifier.equals(other.lastModifier)) return false;
    if (libraryAlias == null) {
      if (other.libraryAlias != null) return false;
    } else if (!libraryAlias.equals(other.libraryAlias)) return false;
    if (libraryBarcode == null) {
      if (other.libraryBarcode != null) return false;
    } else if (!libraryBarcode.equals(other.libraryBarcode)) return false;
    if (libraryDescription == null) {
      if (other.libraryDescription != null) return false;
    } else if (!libraryDescription.equals(other.libraryDescription)) return false;
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
    if (libraryLowQuality != other.libraryLowQuality) return false;
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

  @Override
  public long getId() {
    return aliquotId;
  }

  @Override
  public void setId(long id) {
    this.aliquotId = id;
  }

  @Override
  public boolean isSaved() {
    return aliquotId != LibraryAliquot.UNSAVED_ID;
  }

}
