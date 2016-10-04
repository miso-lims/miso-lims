/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.event.model.PoolEvent;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.PooledElementDeserializer;

/**
 * Skeleton implementation of a Pool
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonIgnoreProperties({ "lastModifier", "hasLowQualityMembers" })
public abstract class AbstractPool<P extends Poolable<?, ?>> extends AbstractBoxable implements Pool<P> {
  protected static final Logger log = LoggerFactory.getLogger(AbstractPool.class);

  public static final Long UNSAVED_ID = 0L;
  public static final String CONCENTRATION_UNITS = "ng/µL";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long poolId = AbstractPool.UNSAVED_ID;

  @OneToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile;

  private String name;
  private String description;

  private Collection<P> pooledElements = new HashSet<P>();
  private Collection<Experiment> experiments = new HashSet<Experiment>();
  private Date creationDate;
  private Double concentration;
  private String identificationBarcode;
  private boolean readyToRun = false;

  private final Collection<PoolQC> poolQCs = new TreeSet<PoolQC>();
  private Boolean qcPassed;

  private Date lastUpdated;

  // listeners
  private final Set<MisoListener> listeners = new HashSet<MisoListener>();
  private Set<User> watchers = new HashSet<User>();
  private final Collection<ChangeLog> changeLog = new ArrayList<ChangeLog>();
  private User lastModifier;
  private Date lastModified;

  @Transient
  private Collection<Note> notes = new HashSet<Note>();

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
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public long getId() {
    return poolId;
  }

  @Override
  public void setId(long id) {
    this.poolId = id;
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
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void addPoolableElement(P poolable) throws MalformedDilutionException {
    pooledElements.add(poolable);
  }

  @SuppressWarnings("unchecked")
  @Override
  @JsonDeserialize(using = PooledElementDeserializer.class)
  public <T extends Poolable> void setPoolableElements(Collection<T> poolables) {
    if (poolables == null) {
      if (this.pooledElements == null) {
        this.pooledElements = Collections.emptySet();
      }
    } else {
      this.pooledElements = (Collection<P>) poolables;
    }
  }

  @Override
  public Collection<P> getPoolableElements() {
    return this.pooledElements;
  }

  /**
   * Convenience method to return Dilutions from this Pool given that the Pooled Elements may well either be a set of single dilutions, or a
   * single plate comprising a number of dilutions within that plate, or something else entirely
   * 
   * @return Collection<Dilution> dilutions.
   */
  @Override
  public Collection<? extends Dilution> getDilutions() {
    Set<Dilution> allDilutions = new HashSet<Dilution>();
    for (Poolable<?, ?> poolable : getPoolableElements()) {
      if (poolable instanceof Dilution) {
        allDilutions.add((Dilution) poolable);
      }
    }
    return allDilutions;
  }

  @Override
  public void addExperiment(Experiment experiment) throws MalformedExperimentException {
    if (experiment != null) {
      experiments.add(experiment);
      experiment.setPool(this);
    }
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
  public Collection<Experiment> getExperiments() {
    return experiments;
  }

  @Override
  public Date getCreationDate() {
    return this.creationDate;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public Double getConcentration() {
    return this.concentration;
  }

  @Override
  public void setConcentration(Double concentration) {
    this.concentration = concentration;
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
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public boolean getReadyToRun() {
    return readyToRun;
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
  public void addQc(PoolQC poolQc) throws MalformedPoolQcException {
    this.poolQCs.add(poolQc);
    try {
      poolQc.setPool(this);
    } catch (MalformedPoolException e) {
      log.error("add QC", e);
    }
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
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
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
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
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
  public Set<MisoListener> getListeners() {
    return this.listeners;
  }

  @Override
  public boolean addListener(MisoListener listener) {
    return listeners.add(listener);
  }

  @Override
  public boolean removeListener(MisoListener listener) {
    return listeners.remove(listener);
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
  public Set<User> getWatchers() {
    return watchers;
  }

  @Override
  public void setWatchers(Set<User> watchers) {
    this.watchers = watchers;
  }

  @Override
  public void addWatcher(User user) {
    watchers.add(user);
  }

  @Override
  public void removeWatcher(User user) {
    watchers.remove(user);
  }

  @Override
  public String getWatchableIdentifier() {
    return getName();
  }

  @Override
  public boolean isDeletable() {
    return getId() != AbstractPool.UNSAVED_ID && getPoolableElements().isEmpty();
  }

  @Override
  @JsonIgnore
  public boolean getHasLowQualityMembers() {
    for (Dilution d : getDilutions()) {
      if (d.getLibrary().isLowQuality()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof AbstractPool<?>)) return false;
    AbstractPool<?> other = (AbstractPool<?>) obj;
    return new EqualsBuilder()
        .appendSuper(super.equals(obj))
        .append(description, other.description)
        .append(pooledElements, other.pooledElements)
        .append(experiments, other.experiments)
        .append(concentration, other.concentration)
        .append(identificationBarcode, other.identificationBarcode)
        .append(readyToRun, other.readyToRun)
        .append(qcPassed, other.qcPassed)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(23, 47)
        .appendSuper(super.hashCode())
        .append(description)
        .append(pooledElements)
        .append(experiments)
        .append(concentration)
        .append(identificationBarcode)
        .append(readyToRun)
        .append(qcPassed)
        .toHashCode();
  }

  @Override
  public int compareTo(Object o) {
    Pool<? extends Poolable<?, ?>> t = (Pool<? extends Poolable<?, ?>>) o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    if (!getPoolableElements().isEmpty()) {
      sb.append(" : ");
      sb.append(getPoolableElements());
    }
    return sb.toString();
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
  public String getLocationBarcode() {
    return "";
  }

  private boolean hasDuplicateIndices(Set<String> indices, P item) {
    if (item instanceof Plate<?, ?>) {
      for (Object child : item.getInternalPoolableElements()) {
        @SuppressWarnings("unchecked")
        P childP = (P) child;
        if (hasDuplicateIndices(indices, childP)) {
          return true;
        }
      }
      return false;
    } else if (item instanceof Dilution) {
      Dilution d = (Dilution) item;
      StringBuilder totalIndex = new StringBuilder();
      for (Index index : d.getLibrary().getIndices()) {
        totalIndex.append(index.getSequence());
      }
      return !indices.add(totalIndex.toString());
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public boolean hasDuplicateIndices() {
    Set<String> indices = new HashSet<>();
    for (P item : getPoolableElements()) {
      if (hasDuplicateIndices(indices, item)) {
        return true;
      }
    }
    return false;
  }
}
