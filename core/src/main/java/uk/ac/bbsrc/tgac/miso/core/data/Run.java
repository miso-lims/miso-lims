/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AutoPopulatingList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RunDerivedInfo;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingParametersImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RunChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.event.model.RunEvent;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.util.UnicodeReader;

/**
 * A Run represents a sequencing run on a single sequencing instrument, referenced by a {@link SequencerReference}, comprising one or more
 * {@link SequencerPartitionContainer} objects in which {@link Pool}s are placed on {@link SequencerPoolPartition}s.
 * <p/>
 * Runs can be QCed via {@link RunQC} objects, and are always associated with a given {@link PlatformType}
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Run")
@Inheritance(strategy = InheritanceType.JOINED)
public class Run
    implements SecurableByProfile, Comparable<Run>, Reportable, Watchable, Deletable, Nameable, Alertable, ChangeLoggable, Aliasable {
  private static final Logger log = LoggerFactory.getLogger(Run.class);

  /** Field PREFIX */
  public static final String PREFIX = "RUN";

  private static final Pattern SOLID_RUN_REGEX = Pattern.compile("([A-z0-9\\-]+)_([0-9]{8})_(.*)");
  public static final Long UNSAVED_ID = 0L;

  public static Run createFromSolidXml(String statusXml, Function<String, SequencerReference> findSequencer) {
    try {
      Document statusDoc = SubmissionUtils.emptyDocument();
      SubmissionUtils.transform(new UnicodeReader(statusXml), statusDoc);

      Run run = new Run();
      String runName;
      if (statusDoc.getDocumentElement().getTagName().equals("error")) {
        runName = (statusDoc.getElementsByTagName("RunName").item(0).getTextContent());
      } else {
        runName = (statusDoc.getElementsByTagName("name").item(0).getTextContent());
        if (statusDoc.getElementsByTagName("name").getLength() != 0) {
          for (int i = 0; i < statusDoc.getElementsByTagName("name").getLength(); i++) {
            Element e = (Element) statusDoc.getElementsByTagName("name").item(i);
            Matcher m = SOLID_RUN_REGEX.matcher(e.getTextContent());
            if (m.matches()) {
              runName = e.getTextContent();
            }
          }
        }
        String runStarted = statusDoc.getElementsByTagName("dateStarted").item(0).getTextContent();
        DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        run.setStartDate(logDateFormat.parse(runStarted));

        if (statusDoc.getElementsByTagName("dateCompleted").getLength() != 0) {
          String runCompleted = statusDoc.getElementsByTagName("dateCompleted").item(0).getTextContent();
          run.setCompletionDate(logDateFormat.parse(runCompleted));
        }
      }

      run.setAlias(runName);
      run.setFilePath(runName);
      run.setPairedEnd(false);

      Matcher m = SOLID_RUN_REGEX.matcher(runName);
      String instrument = null;
      if (m.matches()) {
        instrument = m.group(1);
        run.setDescription(m.group(3));
        if (m.group(3).startsWith("MP") || m.group(3).startsWith("PE")) {
          run.setPairedEnd(true);
        }
      } else {
        run.setDescription(runName);
      }
      if (statusDoc.getElementsByTagName("name").getLength() != 0) {
        for (int i = 0; i < statusDoc.getElementsByTagName("name").getLength(); i++) {
          Element e = (Element) statusDoc.getElementsByTagName("name").item(i);
          Matcher me = SOLID_RUN_REGEX.matcher(e.getTextContent());
          if (m.matches()) {
            runName = e.getTextContent();
            instrument = me.group(1);
          }
        }
      }
      run.setSequencerReference(findSequencer.apply(instrument));
      run.setSecurityProfile(new SecurityProfile());
      return run;
    } catch (ParserConfigurationException | TransformerException | ParseException e) {
      log.error("parse status XML", e);
    }
    return null;
  }

  private String accession;

  @Column(nullable = false)
  private String alias;

  @OneToMany(targetEntity = RunChangeLog.class, mappedBy = "run", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLogs = new ArrayList<>();
  @Temporal(TemporalType.DATE)
  private Date completionDate;
  @ManyToMany(targetEntity = SequencerPartitionContainerImpl.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Run_SequencerPartitionContainer", joinColumns = {
      @JoinColumn(name = "Run_runId") }, inverseJoinColumns = {
          @JoinColumn(name = "containers_containerId") })
  private List<SequencerPartitionContainer<SequencerPoolPartition>> containers = new AutoPopulatingList<>(
      SequencerPartitionContainerImpl.class);
  @OneToOne(targetEntity = RunDerivedInfo.class)
  @PrimaryKeyJoinColumn
  private RunDerivedInfo derivedInfo;
  private String description;
  private String filePath;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private HealthType health = HealthType.Unknown;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @Transient
  private final Set<MisoListener> listeners = new HashSet<>();

  private String metrics;

  private String name;

  @ManyToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Run_Note", joinColumns = {
      @JoinColumn(name = "run_runId") }, inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId") })
  private Collection<Note> notes = new HashSet<>();

  @Column(nullable = false)
  private Boolean pairedEnd;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long runId = UNSAVED_ID;

  @OneToMany(targetEntity = RunQCImpl.class, mappedBy = "run", cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<RunQC> runQCs = new TreeSet<>();

  @ManyToOne(targetEntity = SecurityProfile.class, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "securityProfile_profileId")
  private SecurityProfile securityProfile = new SecurityProfile();

  @ManyToOne(targetEntity = SequencerReferenceImpl.class)
  @JoinColumn(name = "sequencerReference_sequencerReferenceId", nullable = false)
  private SequencerReference sequencerReference;

  @ManyToOne(targetEntity = SequencingParametersImpl.class)
  @JoinColumn(name = "sequencingParameters_parametersId")
  private SequencingParameters sequencingParameters;
  @Temporal(TemporalType.DATE)
  private Date startDate;

  @Transient
  // not Hibernate-managed
  private Group watchGroup;

  @ManyToMany(targetEntity = UserImpl.class)
  @JoinTable(name = "Run_Watcher", joinColumns = { @JoinColumn(name = "runId") }, inverseJoinColumns = { @JoinColumn(name = "userId") })
  private Set<User> watchUsers = new HashSet<>();

  /**
   * Construct a new Run with a default empty SecurityProfile
   */
  public Run() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new Run with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public Run(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public boolean addListener(MisoListener listener) {
    return listeners.add(listener);
  }

  public void addNote(Note note) {
    this.notes.add(note);
  }

  public void addQc(RunQC runQC) throws MalformedRunQcException {
    this.runQCs.add(runQC);
    try {
      runQC.setRun(this);
    } catch (MalformedRunException e) {
      log.error("set run QC", e);
    }
    fireRunQcAddedEvent();
  }

  public void addSequencerPartitionContainer(SequencerPartitionContainer<SequencerPoolPartition> f) {
    f.setSecurityProfile(getSecurityProfile());
    if (f.getId() == 0L && f.getIdentificationBarcode() == null) {
      // can't validate it so add it anyway. this will only usually be the case for new run population.
      this.containers.add(f);
    } else {
      if (!this.containers.contains(f)) {
        this.containers.add(f);
      }
    }
  }

  @Override
  public void addWatcher(User user) {
    watchUsers.add(user);
  }

  /**
   * Method buildReport ...
   */

  @Override
  public void buildReport() {
  }

  @Override
  public int compareTo(Run t) {
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
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

  /**
   * Equivalency is based on getRunId() if set, otherwise on name
   */

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Run)) return false;
    Run them = (Run) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == UNSAVED_ID || them.getId() == UNSAVED_ID) {
      return getAlias().equals(them.getAlias());
    } else {
      return getId() == them.getId();
    }
  }

  protected void fireRunCompletedEvent() {
    if (this.getId() != 0L) {
      RunEvent re = new RunEvent(this, MisoEventType.RUN_COMPLETED, "Run completed");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(re);
      }
    }
  }

  protected void fireRunFailedEvent() {
    if (this.getId() != 0L) {
      RunEvent re = new RunEvent(this, MisoEventType.RUN_FAILED, "Run failed");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(re);
      }
    }
  }

  protected void fireRunQcAddedEvent() {
    if (this.getId() != 0L) {
      RunEvent re = new RunEvent(this, MisoEventType.RUN_QC_ADDED, "Run QC added");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(re);
      }
    }
  }

  protected void fireRunStartedEvent() {
    if (this.getId() != 0L) {
      RunEvent re = new RunEvent(this, MisoEventType.RUN_STARTED, "Run started");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(re);
      }
    }
  }

  protected void fireStatusChangedEvent() {
    switch (getHealth()) {
    case Started:
      fireRunStartedEvent();
      break;
    case Completed:
      fireRunCompletedEvent();
      break;
    case Failed:
      fireRunFailedEvent();
      break;
    default:
      break;

    }
  }

  public String getAccession() {
    return accession;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public Collection<ChangeLog> getChangeLogs() {
    return changeLogs;
  }

  public Date getCompletionDate() {
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

  public User getLastModifier() {
    return lastModifier;
  }

  public Date getLastUpdated() {
    return (derivedInfo == null ? null : derivedInfo.getLastModified());
  }

  @Override
  public Set<MisoListener> getListeners() {
    return this.listeners;
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

  public Boolean getPairedEnd() {
    return pairedEnd;
  }

  public Collection<RunQC> getRunQCs() {
    return runQCs;
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  public List<SequencerPartitionContainer<SequencerPoolPartition>> getSequencerPartitionContainers() {
    if (this.containers != null) Collections.sort(this.containers);
    return containers;
  }

  public SequencerReference getSequencerReference() {
    return sequencerReference;
  }

  public SequencingParameters getSequencingParameters() {
    return sequencingParameters;
  }

  public Date getStartDate() {
    return startDate;
  }

  @Override
  public String getWatchableIdentifier() {
    return getName();
  }

  @Override
  public Set<User> getWatchers() {
    Set<User> allWatchers = new HashSet<>();
    if (watchGroup != null) allWatchers.addAll(watchGroup.getUsers());
    if (watchUsers != null) allWatchers.addAll(watchUsers);
    return allWatchers;
  }

  public Group getWatchGroup() {
    return watchGroup;
  }

  public Set<User> getWatchUsers() {
    return watchUsers;
  }

  @Override
  public int hashCode() {
    if (getId() != UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getAlias() != null) hashcode = PRIME * hashcode + getAlias().hashCode();
      return hashcode;
    }
  }

  @Override
  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    if (parent.getSecurityProfile().getOwner() != null) {
      setSecurityProfile(parent.getSecurityProfile());
    } else {
      throw new SecurityException("Cannot inherit permissions when parent object owner is not set!");
    }
  }

  @Override
  public boolean isDeletable() {
    return getId() != AbstractQC.UNSAVED_ID;
  }

  @Override
  public boolean removeListener(MisoListener listener) {
    return listeners.remove(listener);
  }

  @Override
  public void removeWatcher(User user) {
    watchUsers.remove(user);
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setCompletionDate(Date completionDate) {
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

  public void setId(long id) {
    this.runId = id;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
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

  public void setPairedEnd(Boolean pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

  public void setQCs(Collection<RunQC> qcs) {
    this.runQCs = qcs;
  }

  @Override
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  public void setSequencerPartitionContainers(List<SequencerPartitionContainer<SequencerPoolPartition>> containers) {
    this.containers = containers;
  }

  public void setSequencerReference(SequencerReference sequencerReference) {
    this.sequencerReference = sequencerReference;
  }

  public void setSequencingParameters(SequencingParameters parameters) {
    this.sequencingParameters = parameters;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public void setWatchGroup(Group watchGroup) {
    this.watchGroup = watchGroup;
  }

  public void setWatchUsers(Set<User> watchUsers) {
    this.watchUsers = watchUsers;
  }

  @Override
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }
}
