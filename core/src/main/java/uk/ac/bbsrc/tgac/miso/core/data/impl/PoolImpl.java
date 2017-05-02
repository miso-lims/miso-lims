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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxable;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.PoolBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.event.model.PoolEvent;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Pool")
public class PoolImpl extends AbstractBoxable implements Pool, Serializable {
  private static final int CONCENTRATION_LENGTH = 17;
  public static final String CONCENTRATION_UNITS = "nM";
  private static final int DESCRIPTION_LENGTH = 255;
  private static final int ID_BARCODE_LENGTH = 255;
  private static final Logger log = LoggerFactory.getLogger(PoolImpl.class);
  private static final int NAME_LENGTH = 255;
  public static final String PREFIX = "IPO";
  private static final long serialVersionUID = 1L;
  public static final Long UNSAVED_ID = 0L;

  @OneToMany(targetEntity = PoolChangeLog.class, mappedBy = "pool")
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @Column(length = CONCENTRATION_LENGTH)
  private Double concentration;

  @Temporal(TemporalType.DATE)
  private Date creationDate;
  @OneToOne(targetEntity = PoolDerivedInfo.class)
  @PrimaryKeyJoinColumn
  private PoolDerivedInfo derivedInfo;
  @Column(length = DESCRIPTION_LENGTH)
  private String description;

  @OneToMany(targetEntity = ExperimentImpl.class, mappedBy = "pool")
  @JsonManagedReference
  private Collection<Experiment> experiments = new HashSet<>();

  @Column(length = ID_BARCODE_LENGTH)
  private String identificationBarcode;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  // listeners
  @Transient
  private final Set<MisoListener> listeners = new HashSet<>();

  @Column(length = NAME_LENGTH)
  private String name;

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Pool_Note", joinColumns = {
      @JoinColumn(name = "pool_poolId") }, inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId") })
  private Collection<Note> notes = new HashSet<>();

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @ManyToMany(targetEntity = PoolableElementView.class)
  @JoinTable(name = "Pool_Dilution", joinColumns = {
      @JoinColumn(name = "pool_poolId") }, inverseJoinColumns = {
      @JoinColumn(name = "dilution_dilutionId") })
  private Set<PoolableElementView> pooledElementViews = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long poolId = PoolImpl.UNSAVED_ID;

  @OneToMany(targetEntity = PoolQCImpl.class, mappedBy = "pool")
  @JsonManagedReference
  private final Collection<PoolQC> poolQCs = new TreeSet<>();

  @OneToOne(optional = true)
  @PrimaryKeyJoinColumn
  private PoolBoxPosition boxPosition;

  private Boolean qcPassed;

  @Column(name = "ready")
  private boolean readyToRun = false;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "securityProfile_profileId")
  private SecurityProfile securityProfile;

  @Transient
  private String units = "";

  @Transient
  // not Hibernate-managed
  private Group watchGroup;

  @ManyToMany(targetEntity = UserImpl.class)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(name = "Pool_Watcher", joinColumns = { @JoinColumn(name = "poolId") }, inverseJoinColumns = { @JoinColumn(name = "userId") })
  private Set<User> watchUsers = new HashSet<>();

  public PoolImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  public PoolImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public void addExperiment(Experiment experiment) throws MalformedExperimentException {
    if (experiment != null) {
      experiments.add(experiment);
      experiment.setPool(this);
    }
  }

  @Override
  public boolean addListener(MisoListener listener) {
    return listeners.add(listener);
  }

  @Override
  public void addNote(Note note) {
    this.notes.add(note);
  }

  @Override
  public void addQc(PoolQC poolQc) throws MalformedPoolQcException {
    this.poolQCs.add(poolQc);
    try {
      poolQc.setPool(this);
    } catch (MalformedPoolException e) {
      log.error("add QC", e);
    }
  }

  @Override
  public void addWatcher(User user) {
    watchUsers.add(user);
  }

  @Override
  public int compareTo(Pool t) {
    if (getId() != 0L && t.getId() != 0L) {
      if (getId() < t.getId()) return -1;
      if (getId() > t.getId()) return 1;
    } else if (getName() != null && t.getName() != null) {
      return getName().compareTo(t.getName());
    } else if (getAlias() != null && t.getAlias() != null) {
      return getAlias().compareTo(t.getAlias());
    }
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Pool)) return false;
    Pool other = (Pool) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(description, other.getDescription())
        .append(pooledElementViews, other.getPoolableElementViews()).append(experiments, other.getExperiments())
        .append(concentration, other.getConcentration())
        .append(identificationBarcode, other.getIdentificationBarcode()).append(readyToRun, other.getReadyToRun())
        .append(qcPassed, other.getQcPassed())
        .isEquals();
  }

  protected void firePoolReadyEvent() {
    if (this.getId() != 0L) {
      PoolEvent pe = new PoolEvent(this, MisoEventType.POOL_READY, "Pool " + getName() + " ready to run");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(pe);
      }
    }
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
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public Double getConcentration() {
    return this.concentration;
  }

  @Override
  public Date getCreationDate() {
    return this.creationDate;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Collection<Experiment> getExperiments() {
    return experiments;
  }

  @Override
  public boolean getHasLowQualityMembers() {
    for (PoolableElementView d : getPoolableElementViews()) {
      if (d.isLowQualityLibrary()) {
        return true;
      }
    }
    return false;
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
  public Date getLastModified() {
    return (derivedInfo == null ? null : derivedInfo.getLastModified());
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public Set<MisoListener> getListeners() {
    return this.listeners;
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
  public Collection<Note> getNotes() {
    return notes;
  }

  @Override
  public PlatformType getPlatformType() {
    return platformType;
  }

  @Override
  public Collection<PoolQC> getPoolQCs() {
    return poolQCs;
  }

  @Override
  public Boolean getQcPassed() {
    return qcPassed;
  }

  @Override
  public boolean getReadyToRun() {
    return readyToRun;
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  public String getUnits() {
    return this.units;
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
  public boolean hasDuplicateIndices() {
    Set<String> indices = new HashSet<>();
    for (PoolableElementView item : getPoolableElementViews()) {
      if (hasDuplicateIndices(indices, item)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasDuplicateIndices(Set<String> indices, PoolableElementView item) {
    StringBuilder totalIndex = new StringBuilder();
    for (Index index : item.getIndices()) {
      totalIndex.append(index.getSequence());
    }
    return !indices.add(totalIndex.toString());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(23, 47).appendSuper(super.hashCode()).append(description).append(pooledElementViews).append(experiments)
        .append(concentration).append(identificationBarcode).append(readyToRun).append(qcPassed).toHashCode();
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
    return getId() != PoolImpl.UNSAVED_ID && getPoolableElementViews().isEmpty();
  }

  @Override
  public boolean removeListener(MisoListener listener) {
    return listeners.remove(listener);
  }

  @Override
  public void removeWatcher(User user) {
    watchUsers.remove(user);
  }

  @Override
  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setExperiments(Collection<Experiment> experiments) {
    this.experiments = experiments;
    if (experiments != null) {
      for (Experiment e : experiments) {
        if (e != null && e.getPool() == null) {
          e.setPool(this);
        }
      }
    }
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
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public void setName(String name) {
    this.name = name;
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
  public void setReadyToRun(boolean readyToRun) {
    if (!getReadyToRun() && readyToRun) {
      this.readyToRun = readyToRun;
      firePoolReadyEvent();
    } else {
      this.readyToRun = readyToRun;
    }
  }

  @Override
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  @Override
  public void setWatchGroup(Group group) {
    this.watchGroup = group;
  }

  public void setWatchUsers(Set<User> watchUsers) {
    this.watchUsers = watchUsers;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    if (!getPoolableElementViews().isEmpty()) {
      sb.append(" : ");
      sb.append(getPoolableElementViews());
    }
    return sb.toString();
  }

  @Override
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
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
  public void setPoolableElementViews(Set<PoolableElementView> views) {
    if (views == null) {
      this.pooledElementViews = Collections.emptySet();
    } else {
      this.pooledElementViews = views;
    }
  }

  @Override
  public Set<PoolableElementView> getPoolableElementViews() {
    return this.pooledElementViews;
  }

}
