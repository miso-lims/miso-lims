package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.ExperimentChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

/**
 * An Experiment contains design information about a sequencing experiment, as part of a parent
 * {@link Study}.
 */
@Entity
@Table(name = "Experiment")
public class Experiment implements Comparable<Experiment>, Nameable, ChangeLoggable, Deletable, Serializable {
  @Entity
  @Embeddable
  @Table(name = "Experiment_Run_Partition")
  public static class RunPartition implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @ManyToOne
    @JoinColumn(name = "experiment_experimentId")
    private Experiment experiment;
    @Id
    @ManyToOne(targetEntity = PartitionImpl.class)
    @JoinColumn(name = "partition_partitionId")
    private Partition partition;
    @ManyToOne
    @JoinColumn(name = "run_runId")
    private Run run;

    public Experiment getExperiment() {
      return experiment;
    }

    public Partition getPartition() {
      return partition;
    }

    public Run getRun() {
      return run;
    }

    public void setExperiment(Experiment experiment) {
      this.experiment = experiment;
    }

    public void setPartition(Partition partition) {
      this.partition = partition;
    }

    public void setRun(Run run) {
      this.run = run;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((experiment == null) ? 0 : experiment.hashCode());
      result = prime * result + ((partition == null) ? 0 : partition.hashCode());
      result = prime * result + ((run == null) ? 0 : run.hashCode());
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
      RunPartition other = (RunPartition) obj;
      if (experiment == null) {
        if (other.experiment != null)
          return false;
      } else if (!experiment.equals(other.experiment))
        return false;
      if (partition == null) {
        if (other.partition != null)
          return false;
      } else if (!partition.equals(other.partition))
        return false;
      if (run == null) {
        if (other.run != null)
          return false;
      } else if (!run.equals(other.run))
        return false;
      return true;
    }
  }

  private static final long serialVersionUID = 1L;

  private static final Long UNSAVED_ID = 0L;

  private String accession;

  private String alias;

  @OneToMany(targetEntity = ExperimentChangeLog.class, mappedBy = "experiment", cascade = CascadeType.REMOVE)
  private final List<ChangeLog> changeLog = new ArrayList<>();

  private String description;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long experimentId = UNSAVED_ID;

  @ManyToMany(targetEntity = KitImpl.class)
  @JoinTable(name = "Experiment_Kit", inverseJoinColumns = {@JoinColumn(name = "kits_kitId")}, joinColumns = {
      @JoinColumn(name = "experiments_experimentId")})
  private Collection<Kit> kits = new HashSet<>();

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  // defines a library on which this experiment will operate.
  @ManyToOne(targetEntity = LibraryImpl.class)
  @JoinColumn(name = "library_libraryId")
  private Library library;

  @Column(nullable = false)
  private String name;

  @ManyToOne(targetEntity = InstrumentModel.class)
  @JoinColumn(name = "instrumentModelId")
  private InstrumentModel instrumentModel;

  // defines the parent run which processes this experiment
  @OneToMany(mappedBy = "experiment", cascade = CascadeType.ALL)
  private List<RunPartition> runPartitions;

  @ManyToOne(targetEntity = StudyImpl.class)
  @JoinColumn(name = "study_studyId")
  private Study study;
  @Column(nullable = false)
  private String title;

  @CoverageIgnore
  public void addKit(Kit kit) {
    this.kits.add(kit);
  }

  @Override
  @CoverageIgnore
  public int compareTo(Experiment t) {
    if (getId() < t.getId())
      return -1;
    if (getId() > t.getId())
      return 1;
    return 0;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    ExperimentChangeLog changeLogEntry = new ExperimentChangeLog();
    changeLogEntry.setExperiment(this);
    changeLogEntry.setSummary(summary);
    changeLogEntry.setColumnsChanged(columnsChanged);
    changeLogEntry.setUser(user);
    return changeLogEntry;
  }

  /**
   * Equivalency is based on getId() if set, otherwise on name, description and creation date.
   */
  @Override
  @CoverageIgnore
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Experiment))
      return false;
    final Experiment them = (Experiment) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == UNSAVED_ID || them.getId() == UNSAVED_ID) {
      if (getName() != null && them.getName() != null) {
        return getName().equals(them.getName());
      } else {
        return getAlias().equals(them.getAlias());
      }
    } else {
      return getId() == them.getId();
    }
  }

  public String getAccession() {
    return accession;
  }

  public String getAlias() {
    return alias;
  }

  @Override
  public List<ChangeLog> getChangeLog() {
    return changeLog;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public long getId() {
    return experimentId;
  }

  public Collection<Kit> getKits() {
    return kits;
  }

  public Collection<Kit> getKitsByKitType(KitType kitType) {
    final ArrayList<Kit> ks = new ArrayList<>();
    for (final Kit k : kits) {
      if (k.getKitDescriptor().getKitType().equals(kitType)) {
        ks.add(k);
      }
    }
    Collections.sort(ks);
    return ks;
  }

  public Library getLibrary() {
    return library;
  }

  @Override
  public String getName() {
    return name;
  }

  public InstrumentModel getInstrumentModel() {
    return instrumentModel;
  }

  public List<RunPartition> getRunPartitions() {
    return runPartitions;
  }

  public Study getStudy() {
    return study;
  }

  public String getTitle() {
    return title;
  }

  @Override
  @CoverageIgnore
  public int hashCode() {
    if (getId() != UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getName() != null)
        hashcode = PRIME * hashcode + getName().hashCode();
      if (getAlias() != null)
        hashcode = 37 * hashcode + getAlias().hashCode();
      return hashcode;
    }
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setId(long id) {
    this.experimentId = id;
  }

  public void setKits(Collection<Kit> kits) {
    this.kits = kits;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setInstrumentModel(InstrumentModel instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  public void setRunPartitions(List<RunPartition> runPartitions) {
    this.runPartitions = runPartitions;
  }

  public void setStudy(Study study) {
    this.study = study;
  }

  public void setTitle(String title) {
    this.title = title;
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
  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
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
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Experiment";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

}
