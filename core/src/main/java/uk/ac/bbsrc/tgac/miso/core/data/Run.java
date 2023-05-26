package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RunChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * A Run represents a sequencing run on a single sequencing instrument, referenced by a
 * {@link Instrument}, comprising one or more {@link SequencerPartitionContainer} objects in which
 * {@link Pool}s are placed on {@link SequencerPoolPartition}s.
 * <p/>
 * Runs can be QCed via {@link RunQC} objects, and are always associated with a given
 * {@link PlatformType}
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Run")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Run
    implements Comparable<Run>, Nameable, ChangeLoggable, Deletable, Aliasable, Attachable, Serializable {
  private static final long serialVersionUID = 1L;

  /** Field PREFIX */
  public static final String PREFIX = "RUN";

  public static final Long UNSAVED_ID = 0L;

  private String accession;

  @Column(nullable = false)
  private String alias;

  @OneToMany(targetEntity = RunChangeLog.class, mappedBy = "run", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLogs = new ArrayList<>();

  private LocalDate completionDate;

  @OneToMany(mappedBy = "run", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<RunPosition> runPositions;

  private String description;
  private String filePath;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private HealthType health = HealthType.Unknown;

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

  private String metrics;

  private String name;

  @ManyToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Run_Note", joinColumns = {
      @JoinColumn(name = "run_runId")},
      inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId")})
  private Collection<Note> notes = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long runId = UNSAVED_ID;

  @ManyToOne(targetEntity = InstrumentImpl.class)
  @JoinColumn(name = "instrumentId", nullable = false)
  private Instrument sequencer;

  @ManyToOne
  @JoinColumn(name = "sequencingParameters_parametersId")
  private SequencingParameters sequencingParameters;

  @ManyToOne
  @JoinColumn(name = "sequencingKitId")
  private KitDescriptor sequencingKit;

  private String sequencingKitLot;

  private LocalDate startDate;

  @OneToMany(targetEntity = FileAttachment.class)
  @JoinTable(name = "Run_Attachment", joinColumns = {@JoinColumn(name = "runId")}, inverseJoinColumns = {
      @JoinColumn(name = "attachmentId")})
  private List<FileAttachment> attachments;

  @Transient
  private List<FileAttachment> pendingAttachmentDeletions;

  private Boolean qcPassed;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "qcUser")
  private User qcUser;

  private LocalDate qcDate;

  private Boolean dataReview;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "dataReviewerId")
  private User dataReviewer;

  private LocalDate dataReviewDate;

  @Transient
  private String projectsLabel;

  @ManyToOne
  @JoinColumn(name = "sopId")
  private Sop sop;

  @Enumerated(EnumType.STRING)
  private InstrumentDataManglingPolicy dataManglingPolicy;

  /**
   * Construct a new Run with a default empty SecurityProfile
   */
  public Run() {}

  public void addNote(Note note) {
    this.notes.add(note);
  }

  public void addSequencerPartitionContainer(SequencerPartitionContainer f, InstrumentPosition position) {
    RunPosition rp = new RunPosition();
    rp.setRun(this);
    rp.setContainer(f);
    rp.setPosition(position);
    getRunPositions().add(rp);
  }

  @Override
  public int compareTo(Run t) {
    if (getId() < t.getId())
      return -1;
    if (getId() > t.getId())
      return 1;
    return 0;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    RunChangeLog changeLog = new RunChangeLog();
    changeLog.setRun(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Run))
      return false;
    Run them = (Run) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == UNSAVED_ID || them.getId() == UNSAVED_ID) {
      return getAlias().equals(them.getAlias());
    } else {
      return getId() == them.getId();
    }
  }

  public String getAccession() {
    return accession;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public LocalDate getCompletionDate() {
    return completionDate;
  }

  public String getDescription() {
    return description;
  }

  public String getFilePath() {
    return filePath;
  }

  public HealthType getHealth() {
    return health;
  }

  @Override
  public long getId() {
    return runId;
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

  public String getMetrics() {
    return metrics;
  }

  @Override
  public String getName() {
    return name;
  }

  public Collection<Note> getNotes() {
    return notes;
  }

  public List<SequencerPartitionContainer> getSequencerPartitionContainers() {
    return getRunPositions().stream().map(RunPosition::getContainer).collect(Collectors.toList());
  }

  public Set<RunPosition> getRunPositions() {
    if (runPositions == null) {
      runPositions = new HashSet<>();
    }
    return runPositions;
  }

  public Instrument getSequencer() {
    return sequencer;
  }

  public SequencingParameters getSequencingParameters() {
    return sequencingParameters;
  }

  public LocalDate getStartDate() {
    return startDate;
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
    return "run";
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
  public int hashCode() {
    if (getId() != UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getAlias() != null)
        hashcode = PRIME * hashcode + getAlias().hashCode();
      return hashcode;
    }
  }

  public boolean isFull() {
    return getRunPositions().size() >= sequencer.getInstrumentModel().getNumContainers();
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setCompletionDate(LocalDate completionDate) {
    this.completionDate = completionDate;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public void setHealth(HealthType health) {
    if (health.isAllowedFromSequencer()) {
      this.health = health;
    } else {
      throw new IllegalArgumentException("Cannot set a status to " + health.getKey());
    }
  }

  @Override
  public void setId(long id) {
    this.runId = id;
  }

  public void setMetrics(String metrics) {
    this.metrics = metrics;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  public void setSequencer(Instrument sequencer) {
    this.sequencer = sequencer;
  }

  public void setSequencingParameters(SequencingParameters parameters) {
    this.sequencingParameters = parameters;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLogs;
  }

  public Boolean getPairedEnd() {
    return null;
  }

  public void setPairedEnd(boolean pairedEnd) {
    throw new UnsupportedOperationException("Cannot set paired end on runs from this platform.");
  }

  public abstract PlatformType getPlatformType();

  public String getProgress() {
    return getHealth() == HealthType.Running ? "Running" : "";
  }

  public KitDescriptor getSequencingKit() {
    return sequencingKit;
  }

  public void setSequencingKit(KitDescriptor sequencingKit) {
    this.sequencingKit = sequencingKit;
  }

  public String getSequencingKitLot() {
    return sequencingKitLot;
  }

  public void setSequencingKitLot(String sequencingKitLot) {
    this.sequencingKitLot = sequencingKitLot;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public User getQcUser() {
    return qcUser;
  }

  public void setQcUser(User qcUser) {
    this.qcUser = qcUser;
  }

  public LocalDate getQcDate() {
    return qcDate;
  }

  public void setQcDate(LocalDate qcDate) {
    this.qcDate = qcDate;
  }

  public Boolean getDataReview() {
    return dataReview;
  }

  public void setDataReview(Boolean dataReview) {
    this.dataReview = dataReview;
  }

  public User getDataReviewer() {
    return dataReviewer;
  }

  public void setDataReviewer(User dataReviewer) {
    this.dataReviewer = dataReviewer;
  }

  public LocalDate getDataReviewDate() {
    return dataReviewDate;
  }

  public void setDataReviewDate(LocalDate dataReviewDate) {
    this.dataReviewDate = dataReviewDate;
  }

  public String getProjectsLabel() {
    return projectsLabel;
  }

  public void setProjectsLabel(String projectsLabel) {
    this.projectsLabel = projectsLabel;
  }

  public Sop getSop() {
    return sop;
  }

  public void setSop(Sop sop) {
    this.sop = sop;
  }

  public InstrumentDataManglingPolicy getDataManglingPolicy() {
    return dataManglingPolicy;
  }

  public void setDataManglingPolicy(InstrumentDataManglingPolicy dataManglingPolicy) {
    this.dataManglingPolicy = dataManglingPolicy;
  }

}
