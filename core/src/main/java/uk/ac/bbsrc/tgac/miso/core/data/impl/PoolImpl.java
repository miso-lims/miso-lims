package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.hibernate.annotations.Immutable;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxable;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.PoolBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Pool")
public class PoolImpl extends AbstractBoxable implements Pool {
  private static final int DESCRIPTION_LENGTH = 255;
  private static final int ID_BARCODE_LENGTH = 255;
  private static final int NAME_LENGTH = 255;
  public static final String PREFIX = "IPO";
  private static final long serialVersionUID = 1L;
  private static final Long UNSAVED_ID = 0L;

  @OneToMany(targetEntity = PoolChangeLog.class, mappedBy = "pool", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @Column
  private BigDecimal concentration;

  private Integer dnaSize;

  private LocalDate creationDate = LocalDate.now();

  @Column(length = DESCRIPTION_LENGTH)
  private String description;

  @Column(length = ID_BARCODE_LENGTH)
  private String identificationBarcode;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @Column(length = NAME_LENGTH)
  private String name;

  private String alias;

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Pool_Note", joinColumns = {
      @JoinColumn(name = "pool_poolId")},
      inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId")})
  private Collection<Note> notes = new HashSet<>();

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @OneToMany(mappedBy = "pool", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<PoolElement> poolElements = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long poolId = PoolImpl.UNSAVED_ID;

  @OneToMany(targetEntity = PoolQC.class, mappedBy = "pool", cascade = CascadeType.REMOVE)
  private final Collection<PoolQC> poolQCs = new TreeSet<>();

  @OneToOne(optional = true)
  @PrimaryKeyJoinColumn
  private PoolBoxPosition boxPosition;

  private Boolean qcPassed;

  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;

  @OneToMany(targetEntity = FileAttachment.class)
  @JoinTable(name = "Pool_Attachment", joinColumns = {@JoinColumn(name = "poolId")}, inverseJoinColumns = {
      @JoinColumn(name = "attachmentId")})
  private List<FileAttachment> attachments;

  @Transient
  private List<FileAttachment> pendingAttachmentDeletions;

  @Immutable
  @ManyToMany
  @JoinTable(name = "Transfer_Pool", joinColumns = {@JoinColumn(name = "poolId")}, inverseJoinColumns = {
      @JoinColumn(name = "transferId")})
  private Set<ListTransferView> listTransferViews;

  @Transient
  private Set<String> duplicateIndicesSequences;

  @Transient
  private Set<String> nearDuplicateIndicesSequences;

  @Transient
  private boolean mergeChild = false;

  public PoolImpl() {}

  @Override
  public Boxable.EntityType getEntityType() {
    return Boxable.EntityType.POOL;
  }

  @Override
  public void addNote(Note note) {
    this.notes.add(note);
  }

  @Override
  public int compareTo(Pool t) {
    if (getId() != 0L && t.getId() != 0L) {
      if (getId() < t.getId())
        return -1;
      if (getId() > t.getId())
        return 1;
    } else if (getName() != null && t.getName() != null) {
      return getName().compareTo(t.getName());
    } else if (getAlias() != null && t.getAlias() != null) {
      return getAlias().compareTo(t.getAlias());
    }
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equalsByIdFirst(this, obj,
        PoolImpl::getDescription,
        PoolImpl::getPoolContents,
        PoolImpl::getConcentration,
        PoolImpl::getDnaSize,
        PoolImpl::getIdentificationBarcode,
        PoolImpl::getQcPassed);
  }

  @Override
  public int hashCode() {
    return LimsUtils.hashCodeByIdFirst(this,
        description,
        poolElements,
        concentration,
        dnaSize,
        identificationBarcode,
        qcPassed);
  }

  @Override
  public boolean isMergeChild() {
    return mergeChild;
  }

  @Override
  public void makeMergeChild() {
    mergeChild = true;
  }

  @Override
  public Box getBox() {
    return boxPosition == null ? null : boxPosition.getBox();
  }

  @Override
  public String getBoxPosition() {
    return boxPosition == null ? null : boxPosition.getPosition();
  }

  @Override
  public void setBoxPosition(PoolBoxPosition boxPosition) {
    this.boxPosition = boxPosition;
  }

  @Override
  public void removeFromBox() {
    this.boxPosition = null;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public BigDecimal getConcentration() {
    return this.concentration;
  }

  @Override
  public LocalDate getCreationDate() {
    return creationDate;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean getHasLowQualityMembers() {
    return poolElements.stream().map(PoolElement::getAliquot).anyMatch(ListLibraryAliquotView::isLibraryLowQuality);
  }

  @Override
  public long getId() {
    return poolId;
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public String getLocationBarcode() {
    return "";
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public Collection<Note> getNotes() {
    return notes;
  }

  @Override
  public PlatformType getPlatformType() {
    return platformType;
  }

  @Override
  public Collection<PoolQC> getQCs() {
    return poolQCs;
  }

  @Override
  public Boolean getQcPassed() {
    return qcPassed;
  }

  @Override
  public List<FileAttachment> getAttachments() {
    return attachments;
  }

  @Override
  public void setAttachments(List<FileAttachment> attachments) {
    this.attachments = attachments;
  }

  @Override
  public String getAttachmentsTarget() {
    return "pool";
  }

  @Override
  public List<FileAttachment> getPendingAttachmentDeletions() {
    return pendingAttachmentDeletions;
  }

  @Override
  public void setPendingAttachmentDeletions(List<FileAttachment> pendingAttachmentDeletions) {
    this.pendingAttachmentDeletions = pendingAttachmentDeletions;
  }

  @Override
  public boolean hasLibrariesWithoutIndex() {
    return getPoolContents().stream()
        .map(element -> element.getAliquot().getParentLibrary())
        .anyMatch(lib -> lib.getIndex1() == null);
  }

  @Override
  public void setConcentration(BigDecimal concentration) {
    this.concentration = concentration;
  }

  @Override
  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setId(long id) {
    this.poolId = id;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = nullifyStringIfBlank(identificationBarcode);
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  @Override
  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  @Override
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  @Override
  public Long getPreMigrationId() {
    return null;
  }

  @Override
  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  @Override
  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  @Override
  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  @Override
  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    if (!getPoolContents().isEmpty()) {
      sb.append(" : ");
      sb.append(getPoolContents());
    }
    return sb.toString();
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    PoolChangeLog changeLog = new PoolChangeLog();
    changeLog.setPool(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public Set<PoolElement> getPoolContents() {
    return poolElements;
  }

  @Override
  public void setPoolElements(Set<PoolElement> poolElements) {
    this.poolElements = poolElements;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  @Override
  public Date getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(Date created) {
    this.creationTime = created;
  }

  @Override
  public Integer getDnaSize() {
    return dnaSize;
  }

  public void setDnaSize(Integer dnaSize) {
    this.dnaSize = dnaSize;
  }

  @Override
  public String getLongestIndex() {
    return LimsUtils.getLongestIndex(getPoolContents().stream()
        .map(element -> element.getAliquot().getParentLibrary())
        .collect(Collectors.toList()));
  }

  @Override
  public QcTarget getQcTarget() {
    return QcTarget.Pool;
  }

  @Override
  public LocalDate getBarcodeDate() {
    return getCreationDate();
  }


  @Override
  public String getDeleteType() {
    return "Pool";
  }

  @Override
  public String getDeleteDescription() {
    return getName() + (getAlias() == null ? "" : " (" + getAlias() + ")");
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public Set<String> getPrioritySubprojectAliases() {
    return poolElements.stream()
        .map(PoolElement::getAliquot)
        .filter(view -> view.getSubprojectPriority() != null && view.getSubprojectPriority())
        .map(view -> view.getSubprojectAlias()).collect(Collectors.toSet());
  }

  @Override
  public Set<ListTransferView> getTransferViews() {
    if (listTransferViews == null) {
      listTransferViews = new HashSet<>();
    }
    return listTransferViews;
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitPool(this);
  }

}
