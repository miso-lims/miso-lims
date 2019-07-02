package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
public class Workset implements Serializable, Aliasable, Timestamped, Deletable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @Column(name = "worksetId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String alias;

  private String description;

  @ManyToMany(targetEntity = SampleImpl.class)
  @JoinTable(name = "Workset_Sample", joinColumns = { @JoinColumn(name = "worksetId") }, inverseJoinColumns = {
      @JoinColumn(name = "sampleId") })
  private Set<Sample> samples;

  @ManyToMany(targetEntity = LibraryImpl.class)
  @JoinTable(name = "Workset_Library", joinColumns = { @JoinColumn(name = "worksetId") }, inverseJoinColumns = {
      @JoinColumn(name = "libraryId") })
  private Set<Library> libraries;

  @ManyToMany(targetEntity = LibraryAliquot.class)
  @JoinTable(name = "Workset_LibraryAliquot", joinColumns = { @JoinColumn(name = "worksetId") }, inverseJoinColumns = {
      @JoinColumn(name = "aliquotId") })
  private Set<LibraryAliquot> libraryAliquots;

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

  public Set<Sample> getSamples() {
    if (samples == null) {
      samples = new HashSet<>();
    }
    return samples;
  }

  public void setSamples(Set<Sample> samples) {
    this.samples = samples;
  }

  public Set<Library> getLibraries() {
    if (libraries == null) {
      libraries = new HashSet<>();
    }
    return libraries;
  }

  public void setLibraries(Set<Library> libraries) {
    this.libraries = libraries;
  }

  public Set<LibraryAliquot> getLibraryAliquots() {
    if (libraryAliquots == null) {
      libraryAliquots = new HashSet<>();
    }
    return libraryAliquots;
  }

  public void setLibraryAliquots(Set<LibraryAliquot> aliquots) {
    this.libraryAliquots = aliquots;
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

}
