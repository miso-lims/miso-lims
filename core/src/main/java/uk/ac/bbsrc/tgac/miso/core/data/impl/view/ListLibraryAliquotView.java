package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.hibernate.annotations.Immutable;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingControlType;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryAliquotBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.data.qc.DetailedQcItem;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Immutable
@Table(name = "LibraryAliquot")
public class ListLibraryAliquotView
    implements DetailedQcItem, Identifiable, Serializable, Comparable<ListLibraryAliquotView> {

  private static final long serialVersionUID = 1L;

  @Id
  private long aliquotId = LibraryAliquot.UNSAVED_ID;

  @ManyToOne
  @JoinColumn(name = "parentAliquotId")
  private ParentAliquot parentAliquot;

  private String name;
  private String alias;
  private Integer dnaSize;
  private BigDecimal concentration;

  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;

  private String identificationBarcode;
  private BigDecimal volume;

  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;

  private BigDecimal ngUsed;
  private BigDecimal volumeUsed;
  private boolean discarded;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;
  private String detailedQcStatusNote;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "qcUser")
  private User qcUser;

  private LocalDate qcDate;

  @ManyToOne
  @JoinColumn(name = "libraryDesignCodeId")
  private LibraryDesignCode designCode;

  private Long preMigrationId;
  private Long targetedSequencingId;
  private Date created;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @OneToOne(optional = true)
  @PrimaryKeyJoinColumn
  private LibraryAliquotBoxPosition boxPosition;

  @Immutable
  @ManyToMany
  @JoinTable(name = "Transfer_LibraryAliquot", joinColumns = {@JoinColumn(name = "aliquotId")}, inverseJoinColumns = {
      @JoinColumn(name = "transferId")})
  private Set<ListTransferView> listTransferViews;

  @ManyToOne
  @JoinColumn(name = "libraryId")
  private ParentLibrary parentLibrary;

  public ParentAliquot getParentAliquot() {
    return parentAliquot;
  }

  public void setParentAliquot(ParentAliquot parentAliquot) {
    this.parentAliquot = parentAliquot;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Integer getDnaSize() {
    return dnaSize;
  }

  public void setDnaSize(Integer dnaSize) {
    this.dnaSize = dnaSize;
  }

  public BigDecimal getConcentration() {
    return concentration;
  }

  public void setConcentration(BigDecimal concentration) {
    this.concentration = concentration;
  }

  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  public BigDecimal getNgUsed() {
    return ngUsed;
  }

  public void setNgUsed(BigDecimal ngUsed) {
    this.ngUsed = ngUsed;
  }

  public BigDecimal getVolumeUsed() {
    return volumeUsed;
  }

  public void setVolumeUsed(BigDecimal volumeUsed) {
    this.volumeUsed = volumeUsed;
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

  @Override
  public User getQcUser() {
    return qcUser;
  }

  @Override
  public void setQcUser(User qcUser) {
    this.qcUser = qcUser;
  }

  @Override
  public LocalDate getQcDate() {
    return qcDate;
  }

  @Override
  public void setQcDate(LocalDate qcDate) {
    this.qcDate = qcDate;
  }

  public LibraryDesignCode getDesignCode() {
    return designCode;
  }

  public void setAliquotDesignCode(LibraryDesignCode aliquotDesignCode) {
    this.designCode = aliquotDesignCode;
  }

  public String getProjectCode() {
    return getProjectAttribute(ParentProject::getCode);
  }

  public String getProjectTitle() {
    return getProjectAttribute(ParentProject::getTitle);
  }

  public Long getProjectId() {
    return getProjectAttribute(ParentProject::getId);
  }

  public String getProjectName() {
    return getProjectAttribute(ParentProject::getName);
  }

  public Long getSubprojectId() {
    return getSubprojectAttribute(ParentSubproject::getId);
  }

  public String getSubprojectAlias() {
    return getSubprojectAttribute(ParentSubproject::getAlias);
  }

  public Boolean getSubprojectPriority() {
    return getSubprojectAttribute(ParentSubproject::isPriority);
  }

  public ParentAttributes getParentAttributes() {
    return getSampleAttribute(ParentSample::getParentAttributes);
  }

  public ParentIdentityAttributes getIdentityAttributes() {
    return getSampleAttribute(ParentSample::getIdentityAttributes);
  }

  public ParentTissueAttributes getTissueAttributes() {
    return getSampleAttribute(ParentSample::getTissueAttributes);
  }

  public String getAliquotBarcode() {
    return identificationBarcode;
  }

  public void setAliquotBarcode(String aliquotBarcode) {
    this.identificationBarcode = aliquotBarcode;
  }

  public Long getPreMigrationId() {
    return preMigrationId;
  }

  public void setPreMigrationId(Long preMigrationId) {
    this.preMigrationId = preMigrationId;
  }

  public ParentLibrary getParentLibrary() {
    return parentLibrary;
  }

  public void setParentLibrary(ParentLibrary parentLibrary) {
    this.parentLibrary = parentLibrary;
  }

  public Long getLibraryId() {
    return getLibraryAttribute(ParentLibrary::getId);
  }

  public String getLibraryName() {
    return getLibraryAttribute(ParentLibrary::getName);
  }

  public String getLibraryAlias() {
    return getLibraryAttribute(ParentLibrary::getAlias);
  }

  public String getLibraryDescription() {
    return getLibraryAttribute(ParentLibrary::getDescription);
  }

  public Boolean isLibraryLowQuality() {
    return getLibraryAttribute(ParentLibrary::isLowQuality);
  }

  public PlatformType getPlatformType() {
    return getLibraryAttribute(ParentLibrary::getPlatformType);
  }

  public Long getSampleId() {
    return getSampleAttribute(ParentSample::getId);
  }

  public String getSampleName() {
    return getSampleAttribute(ParentSample::getName);
  }

  public String getSampleAlias() {
    return getSampleAttribute(ParentSample::getAlias);
  }

  public String getSampleAccession() {
    return getSampleAttribute(ParentSample::getAccession);
  }

  public SequencingControlType getSampleSequencingControlType() {
    return getSampleAttribute(ParentSample::getSequencingControlType);
  }

  public Date getLastModified() {
    return lastUpdated;
  }

  public void setLastModified(Date lastModified) {
    this.lastUpdated = lastModified;
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
  public int compareTo(ListLibraryAliquotView o) {
    ListLibraryAliquotView t = o;
    if (getId() < t.getId())
      return -1;
    if (getId() > t.getId())
      return 1;
    return 0;
  }

  public Long getTargetedSequencingId() {
    return targetedSequencingId;
  }

  public void setTargetedSequencingId(Long targetedSequencingId) {
    this.targetedSequencingId = targetedSequencingId;
  }

  public BigDecimal getAliquotVolume() {
    return volume;
  }

  public void setAliquotVolume(BigDecimal aliquotVolume) {
    this.volume = aliquotVolume;
  }

  public VolumeUnit getAliquotVolumeUnits() {
    return volumeUnits;
  }

  public void setAliquotVolumeUnits(VolumeUnit aliquotVolumeUnits) {
    this.volumeUnits = aliquotVolumeUnits;
  }

  public boolean isDiscarded() {
    return discarded;
  }

  public void setDiscarded(boolean discarded) {
    this.discarded = discarded;
  }

  public boolean isDistributed() {
    return getTransferViews().stream()
        .anyMatch(ListTransferView::isDistribution);
  }

  public Box getBox() {
    return boxPosition == null ? null : boxPosition.getBox();
  }

  public String getBoxPosition() {
    return boxPosition == null ? null : boxPosition.getPosition();
  }

  public void setBoxPosition(LibraryAliquotBoxPosition boxPosition) {
    this.boxPosition = boxPosition;
  }

  public Set<ListTransferView> getTransferViews() {
    if (listTransferViews == null) {
      listTransferViews = new HashSet<>();
    }
    return listTransferViews;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((created == null) ? 0 : created.hashCode());
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + ((identificationBarcode == null) ? 0 : identificationBarcode.hashCode());
    result = prime * result + ((dnaSize == null) ? 0 : dnaSize.hashCode());
    result = prime * result + ((concentration == null) ? 0 : concentration.hashCode());
    result = prime * result + ((concentrationUnits == null) ? 0 : concentrationUnits.hashCode());
    result = prime * result + (int) (aliquotId ^ (aliquotId >>> 32));
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((ngUsed == null) ? 0 : ngUsed.hashCode());
    result = prime * result + ((volume == null) ? 0 : volume.hashCode());
    result = prime * result + ((volumeUsed == null) ? 0 : volumeUsed.hashCode());
    result = prime * result + ((detailedQcStatus == null) ? 0 : detailedQcStatus.hashCode());
    result = prime * result + ((detailedQcStatusNote == null) ? 0 : detailedQcStatusNote.hashCode());
    result = prime * result + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
    result = prime * result + ((lastModifier == null) ? 0 : lastModifier.hashCode());
    result = prime * result + ((preMigrationId == null) ? 0 : preMigrationId.hashCode());
    result = prime * result + ((targetedSequencingId == null) ? 0 : targetedSequencingId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ListLibraryAliquotView other = (ListLibraryAliquotView) obj;
    if (created == null) {
      if (other.created != null)
        return false;
    } else if (!created.equals(other.created))
      return false;
    if (creator == null) {
      if (other.creator != null)
        return false;
    } else if (!creator.equals(other.creator))
      return false;
    if (identificationBarcode == null) {
      if (other.identificationBarcode != null)
        return false;
    } else if (!identificationBarcode.equals(other.identificationBarcode))
      return false;
    if (dnaSize == null) {
      if (other.dnaSize != null)
        return false;
    } else if (!dnaSize.equals(other.dnaSize))
      return false;
    if (concentration == null) {
      if (other.concentration != null)
        return false;
    } else if (!concentration.equals(other.concentration))
      return false;
    if (concentrationUnits == null) {
      if (other.concentrationUnits != null)
        return false;
    } else if (!concentrationUnits.equals(other.concentrationUnits))
      return false;
    if (aliquotId != other.aliquotId)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (ngUsed == null) {
      if (other.ngUsed != null)
        return false;
    } else if (!ngUsed.equals(other.ngUsed))
      return false;
    if (volume == null) {
      if (other.volume != null)
        return false;
    } else if (!volume.equals(other.volume))
      return false;
    if (volumeUsed == null) {
      if (other.volumeUsed != null)
        return false;
    } else if (!volumeUsed.equals(other.volumeUsed))
      return false;
    if (detailedQcStatus == null) {
      if (other.detailedQcStatus != null)
        return false;
    } else if (!detailedQcStatus.equals(other.detailedQcStatus))
      return false;
    if (detailedQcStatusNote == null) {
      if (other.detailedQcStatusNote != null)
        return false;
    } else if (!detailedQcStatusNote.equals(other.detailedQcStatusNote))
      return false;
    if (lastUpdated == null) {
      if (other.lastUpdated != null)
        return false;
    } else if (!lastUpdated.equals(other.lastUpdated))
      return false;
    if (lastModifier == null) {
      if (other.lastModifier != null)
        return false;
    } else if (!lastModifier.equals(other.lastModifier))
      return false;
    if (preMigrationId == null) {
      if (other.preMigrationId != null)
        return false;
    } else if (!preMigrationId.equals(other.preMigrationId))
      return false;
    if (targetedSequencingId == null) {
      if (other.targetedSequencingId != null)
        return false;
    } else if (!targetedSequencingId.equals(other.targetedSequencingId))
      return false;
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

  private <T> T getLibraryAttribute(Function<ParentLibrary, T> getter) {
    return parentLibrary == null ? null : getter.apply(parentLibrary);
  }

  private <T> T getSampleAttribute(Function<ParentSample, T> getter) {
    if (parentLibrary == null || parentLibrary.getParentSample() == null) {
      return null;
    }
    return getter.apply(parentLibrary.getParentSample());
  }

  private <T> T getProjectAttribute(Function<ParentProject, T> getter) {
    if (parentLibrary == null || parentLibrary.getParentSample() == null
        || parentLibrary.getParentSample().getParentProject() == null) {
      return null;
    }
    return getter.apply(parentLibrary.getParentSample().getParentProject());
  }

  private <T> T getSubprojectAttribute(Function<ParentSubproject, T> getter) {
    if (parentLibrary == null || parentLibrary.getParentSample() == null
        || parentLibrary.getParentSample().getParentSubproject() == null) {
      return null;
    }
    return getter.apply(parentLibrary.getParentSample().getParentSubproject());
  }

}
