package uk.ac.bbsrc.tgac.miso.core.data.impl.workset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.WorksetChangeLog;

@Entity
public class Workset implements Serializable, Aliasable, ChangeLoggable, Deletable {

  public enum ReservedWord {
    MINE("Mine"), ALL("All"), UNCATEGORIZED("Uncategorized");

    private final String text;

    private ReservedWord(String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }

    public static ReservedWord find(String word) {
      return Stream.of(ReservedWord.values())
          .filter(x -> x.getText().equalsIgnoreCase(word))
          .findAny().orElse(null);
    }
  }

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @Column(name = "worksetId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String alias;

  private String description;

  @ManyToOne
  @JoinColumn(name = "categoryId")
  private WorksetCategory category;

  @ManyToOne
  @JoinColumn(name = "stageId")
  private WorksetStage stage;

  @OneToMany(mappedBy = "workset", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<WorksetSample> worksetSamples;

  @OneToMany(mappedBy = "workset", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<WorksetLibrary> worksetLibraries;

  @OneToMany(mappedBy = "workset", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<WorksetLibraryAliquot> worksetLibraryAliquots;

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

  @OneToMany(targetEntity = WorksetChangeLog.class, mappedBy = "workset", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Workset_Note", joinColumns = {@JoinColumn(name = "worksetId")},
      inverseJoinColumns = {@JoinColumn(name = "noteId")})
  private Set<Note> notes = new HashSet<>();

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public WorksetCategory getCategory() {
    return category;
  }

  public void setCategory(WorksetCategory category) {
    this.category = category;
  }

  public WorksetStage getStage() {
    return stage;
  }

  public void setStage(WorksetStage stage) {
    this.stage = stage;
  }

  public Set<WorksetSample> getWorksetSamples() {
    if (worksetSamples == null) {
      worksetSamples = new HashSet<>();
    }
    return worksetSamples;
  }

  public void addNote(Note note) {
    this.notes.add(note);
  }

  public Set<Note> getNotes() {
    if (notes == null) {
      notes = new HashSet<>();
    }
    return notes;
  }

  public void setNotes(Set<Note> notes) {
    this.notes = notes;
  }

  public void setWorksetSamples(Set<WorksetSample> worksetSamples) {
    this.worksetSamples = worksetSamples;
  }

  public Set<WorksetLibrary> getWorksetLibraries() {
    if (worksetLibraries == null) {
      worksetLibraries = new HashSet<>();
    }
    return worksetLibraries;
  }

  public void setWorksetLibraries(Set<WorksetLibrary> worksetLibraries) {
    this.worksetLibraries = worksetLibraries;
  }

  public Set<WorksetLibraryAliquot> getWorksetLibraryAliquots() {
    if (worksetLibraryAliquots == null) {
      worksetLibraryAliquots = new HashSet<>();
    }
    return worksetLibraryAliquots;
  }

  public void setWorksetLibraryAliquots(Set<WorksetLibraryAliquot> worksetLibraryAliquots) {
    this.worksetLibraryAliquots = worksetLibraryAliquots;
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
    return "Workset";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    WorksetChangeLog change = new WorksetChangeLog();
    change.setWorkset(this);
    change.setSummary(summary);
    change.setColumnsChanged(columnsChanged);
    change.setUser(user);
    change.setTime(new Date());
    return change;
  }

}
