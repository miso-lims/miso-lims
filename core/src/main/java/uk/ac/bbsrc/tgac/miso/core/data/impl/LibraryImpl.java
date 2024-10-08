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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.type.DilutionFactor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

@Entity
@Table(name = "Library")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator")
@DiscriminatorValue("Library")
public class LibraryImpl extends AbstractBoxable implements Library {

  private static final long serialVersionUID = 1L;
  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long libraryId = UNSAVED_ID;

  @Column(nullable = false)
  private String name;

  private String alias;
  private String description;
  private String accession;

  private LocalDate creationDate = LocalDate.now();

  private String identificationBarcode;
  private String locationBarcode;

  @Enumerated(EnumType.STRING)
  @Column(name = "platformType", nullable = false)
  private PlatformType platformType;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;
  private String detailedQcStatusNote;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "qcUser")
  private User qcUser;

  private LocalDate qcDate;

  @Column(nullable = false)
  private boolean lowQuality = false;

  @Column(nullable = false)
  private boolean paired;

  private BigDecimal concentration;

  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;

  private BigDecimal initialVolume;
  private BigDecimal volumeUsed;
  private BigDecimal ngUsed;

  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;

  @ManyToOne
  @JoinColumn(name = "requisitionId")
  private Requisition requisition;

  @ManyToOne
  @JoinColumn(name = "index1Id")
  private Index index1;

  @ManyToOne
  @JoinColumn(name = "index2Id")
  private Index index2;

  @OneToMany(targetEntity = LibraryQC.class, mappedBy = "library", cascade = CascadeType.ALL)
  private final Collection<LibraryQC> libraryQCs = new TreeSet<>();

  @OneToMany(targetEntity = LibraryAliquot.class, mappedBy = "library")
  private final Collection<LibraryAliquot> libraryAliquots = new HashSet<>();

  @ManyToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sample_sampleId")
  private Sample sample;

  @ManyToOne
  @JoinColumn(name = "libraryType")
  private LibraryType libraryType;

  @ManyToOne
  @JoinColumn(name = "librarySelectionType")
  private LibrarySelectionType librarySelectionType;

  @ManyToOne
  @JoinColumn(name = "libraryStrategyType")
  private LibraryStrategyType libraryStrategyType;

  @ManyToOne
  @JoinColumn(name = "workstationId")
  private Workstation workstation;

  @ManyToOne(targetEntity = InstrumentImpl.class)
  @JoinColumn(name = "thermalCyclerId")
  private Instrument thermalCycler;

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

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Library_Note", joinColumns = {
      @JoinColumn(name = "library_libraryId")},
      inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId")})
  private Collection<Note> notes = new HashSet<>();

  @OneToMany(targetEntity = LibraryChangeLog.class, mappedBy = "library", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @OneToOne(optional = true)
  @PrimaryKeyJoinColumn
  private LibraryBoxPosition boxPosition;

  private Integer dnaSize;

  @ManyToOne
  @JoinColumn(name = "kitDescriptorId")
  private KitDescriptor kitDescriptor;

  private String kitLot;

  @ManyToOne
  @JoinColumn(name = "spikeInId")
  private LibrarySpikeIn spikeIn;

  private BigDecimal spikeInVolume;

  @Enumerated(EnumType.STRING)
  private DilutionFactor spikeInDilutionFactor;

  private boolean umis;

  @ManyToOne
  @JoinColumn(name = "sopId")
  private Sop sop;

  @Immutable
  @ManyToMany
  @JoinTable(name = "Transfer_Library", joinColumns = {@JoinColumn(name = "libraryId")}, inverseJoinColumns = {
      @JoinColumn(name = "transferId")})
  private Set<ListTransferView> listTransferViews;

  @OneToMany(targetEntity = FileAttachment.class)
  @JoinTable(name = "Library_Attachment", joinColumns = {@JoinColumn(name = "libraryId")}, inverseJoinColumns = {
      @JoinColumn(name = "attachmentId")})
  private List<FileAttachment> attachments;

  @Transient
  private List<FileAttachment> pendingAttachmentDeletions;

  @Transient
  private TransferLibrary creationReceiptInfo;

  @Override
  public Boxable.EntityType getEntityType() {
    return Boxable.EntityType.LIBRARY;
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
  public void setBoxPosition(LibraryBoxPosition boxPosition) {
    this.boxPosition = boxPosition;
  }

  @Override
  public void removeFromBox() {
    this.boxPosition = null;
  }

  @Override
  public long getId() {
    return libraryId;
  }

  @Override
  public void setId(long id) {
    this.libraryId = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getAccession() {
    return accession;
  }

  @Override
  public void setAccession(String accession) {
    this.accession = accession;
  }

  @Override
  public LocalDate getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = nullifyStringIfBlank(identificationBarcode);
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  @Override
  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = nullifyStringIfBlank(locationBarcode);
  }

  @CoverageIgnore
  @Override
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public Index getIndex1() {
    return index1;
  }

  @Override
  public void setIndex1(Index index1) {
    this.index1 = index1;
  }

  @Override
  public Index getIndex2() {
    return index2;
  }

  @Override
  public void setIndex2(Index index2) {
    this.index2 = index2;
  }

  @Override
  public Boolean getPaired() {
    return paired;
  }

  @Override
  public void setPaired(Boolean paired) {
    this.paired = paired;
  }

  @Override
  public Collection<LibraryQC> getQCs() {
    return libraryQCs;
  }

  @Override
  public void addLibraryAliquot(LibraryAliquot libraryAliquot) {
    this.libraryAliquots.add(libraryAliquot);
    libraryAliquot.setLibrary(this);
  }

  @Override
  public Collection<LibraryAliquot> getLibraryAliquots() {
    return libraryAliquots;
  }

  @Override
  public Sample getSample() {
    return sample;
  }

  @Override
  public void setSample(Sample sample) {
    this.sample = sample;
  }

  @Override
  public LibraryType getLibraryType() {
    return libraryType;
  }

  @Override
  public void setLibraryType(LibraryType libraryType) {
    this.libraryType = libraryType;
  }

  @Override
  public LibrarySelectionType getLibrarySelectionType() {
    return librarySelectionType;
  }

  @Override
  public void setLibrarySelectionType(LibrarySelectionType librarySelectionType) {
    this.librarySelectionType = librarySelectionType;
  }

  @Override
  public LibraryStrategyType getLibraryStrategyType() {
    return libraryStrategyType;
  }

  @Override
  public void setLibraryStrategyType(LibraryStrategyType libraryStrategyType) {
    this.libraryStrategyType = libraryStrategyType;
  }

  @Override
  public PlatformType getPlatformType() {
    return platformType;
  }

  @Override
  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  @Override
  public void setPlatformType(String platformName) {
    this.platformType = PlatformType.get(platformName);
  }

  @Override
  public BigDecimal getConcentration() {
    return concentration;
  }

  @Override
  public void setConcentration(BigDecimal concentration) {
    this.concentration = concentration;
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

  @Override
  public Collection<Note> getNotes() {
    return notes;
  }

  @Override
  public void addNote(Note note) {
    this.notes.add(note);
  }

  @Override
  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  @Override
  public void setLowQuality(boolean lowquality) {
    lowQuality = lowquality;
  }

  @Override
  public boolean isLowQuality() {
    return lowQuality;
  }

  @Override
  public Long getPreMigrationId() {
    return null;
  }

  @CoverageIgnore
  @Override
  public int compareTo(Library l) {
    if (getId() != 0L && l.getId() != 0L) {
      if (getId() < l.getId())
        return -1;
      if (getId() > l.getId())
        return 1;
    } else if (getName() != null && l.getName() != null) {
      return getName().compareTo(l.getName());
    } else if (getAlias() != null && l.getAlias() != null) {
      return getAlias().compareTo(l.getAlias());
    }
    return 0;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getName());
    sb.append(" : ");
    sb.append(getAlias());
    sb.append(" : ");
    sb.append(getDescription());
    return sb.toString();
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
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public QcTarget getQcTarget() {
    return QcTarget.Library;
  }

  @Override
  public Integer getDnaSize() {
    return dnaSize;
  }

  @Override
  public void setDnaSize(Integer dnaSize) {
    this.dnaSize = dnaSize;
  }

  @Override
  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  @Override
  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

  @Override
  public String getKitLot() {
    return kitLot;
  }

  @Override
  public void setKitLot(String kitLot) {
    this.kitLot = kitLot;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(3, 33)
        .appendSuper(super.hashCode())
        .append(libraryId)
        .append(accession)
        .append(getAlias())
        .append(description)
        .append(identificationBarcode)
        .append(index1)
        .append(index2)
        .append(concentration)
        .append(librarySelectionType)
        .append(libraryStrategyType)
        .append(libraryType)
        .append(locationBarcode)
        .append(lowQuality)
        .append(paired)
        .append(platformType)
        .append(detailedQcStatus)
        .append(detailedQcStatusNote)
        .append(kitDescriptor)
        .append(kitLot)
        .append(sop)
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
    LibraryImpl other = (LibraryImpl) obj;
    return new EqualsBuilder()
        .appendSuper(super.equals(obj))
        .append(libraryId, other.libraryId)
        .append(accession, other.accession)
        .append(getAlias(), other.getAlias())
        .append(description, other.description)
        .append(identificationBarcode, other.identificationBarcode)
        .append(index1, other.index1)
        .append(index2, other.index2)
        .append(concentration, other.concentration)
        .append(librarySelectionType, other.librarySelectionType)
        .append(libraryStrategyType, other.libraryStrategyType)
        .append(libraryType, other.libraryType)
        .append(locationBarcode, other.locationBarcode)
        .append(lowQuality, other.lowQuality)
        .append(paired, other.paired)
        .append(platformType, other.platformType)
        .append(detailedQcStatus, other.detailedQcStatus)
        .append(detailedQcStatusNote, other.detailedQcStatusNote)
        .append(kitDescriptor, other.kitDescriptor)
        .append(kitLot, other.kitLot)
        .append(sop, other.sop)
        .isEquals();
  }

  @Override
  public LocalDate getBarcodeDate() {
    return getCreationDate();
  }


  @Override
  public String getDeleteType() {
    return "Library";
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
  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  @Override
  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  @Override
  public BigDecimal getInitialVolume() {
    return initialVolume;
  }

  @Override
  public void setInitialVolume(BigDecimal initialVolume) {
    this.initialVolume = initialVolume;
  }

  @Override
  public BigDecimal getVolumeUsed() {
    return volumeUsed;
  }

  @Override
  public void setVolumeUsed(BigDecimal volumeUsed) {
    this.volumeUsed = volumeUsed;
  }

  @Override
  public BigDecimal getNgUsed() {
    return ngUsed;
  }

  @Override
  public void setNgUsed(BigDecimal ngUsed) {
    this.ngUsed = ngUsed;
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
  public Requisition getRequisition() {
    return requisition;
  }

  @Override
  public void setRequisition(Requisition requisition) {
    this.requisition = requisition;
  }

  @Override
  public LibrarySpikeIn getSpikeIn() {
    return spikeIn;
  }

  @Override
  public void setSpikeIn(LibrarySpikeIn spikeIn) {
    this.spikeIn = spikeIn;
  }

  @Override
  public BigDecimal getSpikeInVolume() {
    return spikeInVolume;
  }

  @Override
  public void setSpikeInVolume(BigDecimal spikeInVolume) {
    this.spikeInVolume = spikeInVolume;
  }

  @Override
  public DilutionFactor getSpikeInDilutionFactor() {
    return spikeInDilutionFactor;
  }

  @Override
  public void setSpikeInDilutionFactor(DilutionFactor dilutionFactor) {
    this.spikeInDilutionFactor = dilutionFactor;
  }

  @Override
  public boolean getUmis() {
    return umis;
  }

  @Override
  public void setUmis(boolean umis) {
    this.umis = umis;
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
    return "library";
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
  public Sample getParent() {
    return getSample();
  }

  @Override
  public Set<ListTransferView> getTransferViews() {
    if (listTransferViews == null) {
      listTransferViews = new HashSet<>();
    }
    return listTransferViews;
  }

  @Override
  public Workstation getWorkstation() {
    return workstation;
  }

  @Override
  public void setWorkstation(Workstation workstation) {
    this.workstation = workstation;
  }

  @Override
  public Instrument getThermalCycler() {
    return thermalCycler;
  }

  @Override
  public void setThermalCycler(Instrument thermalCycler) {
    this.thermalCycler = thermalCycler;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    LibraryChangeLog changeLog = new LibraryChangeLog();
    changeLog.setLibrary(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitLibrary(this);
  }

  @Override
  public TransferLibrary getCreationReceiptInfo() {
    return creationReceiptInfo;
  }

  @Override
  public void setCreationReceiptInfo(TransferLibrary creationReceiptInfo) {
    this.creationReceiptInfo = creationReceiptInfo;
  }

  @Override
  public Sop getSop() {
    return sop;
  }

  @Override
  public void setSop(Sop sop) {
    this.sop = sop;
  }

  @Override
  public String getBatchId() {
    return LibraryBatch.generateId(this);
  }

}
