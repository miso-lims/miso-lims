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

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.event.model.PoolEvent;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.PooledElementDeserializer;

import javax.persistence.*;
import java.util.*;

/**
 * Skeleton implementation of a Pool
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class AbstractPool<P extends Poolable> implements Pool<P> {
  protected static final Logger log = LoggerFactory.getLogger(AbstractPool.class);

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long poolId = AbstractPool.UNSAVED_ID;

  @OneToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile;  

  private String name;
  private String alias;

  private Collection<P> pooledElements = new HashSet<P>();
  private Collection<Experiment> experiments = new HashSet<Experiment>();
  private Date creationDate;
  private Double concentration;
  private String identificationBarcode;
  private boolean readyToRun = false;

  private Collection<PoolQC> poolQCs = new TreeSet<PoolQC>();
  private Boolean qcPassed;

  private Date lastUpdated;

  // listeners
  private Set<MisoListener> listeners = new HashSet<MisoListener>();
  private Set<User> watchers = new HashSet<User>();

  @Deprecated
  public Long getPoolId() {
    return this.poolId;
  }

  @Deprecated
  public void setPoolId(Long poolId) {
    this.poolId = poolId;
  }

  @Override
  public long getId() {
    return poolId;
  }

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
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public void addPoolableElement(P poolable) throws MalformedDilutionException {
    pooledElements.add(poolable);
  }

  @Override
  @JsonDeserialize(using = PooledElementDeserializer.class)
  public <T extends Poolable> void setPoolableElements(Collection<T> poolables) {
    this.pooledElements.clear();
    if (poolables != null && !poolables.isEmpty()) {
      for (T poolable : poolables) {
        if (poolable != null) {
          if (poolable instanceof Poolable) {
            this.pooledElements.add((P)poolable);
          }
          else {
            log.error(poolable.getClass().getName());
          }
        }
        else {
          log.error("Null poolable");
        }
      }
    }
  }

  @Override
  public Collection<P> getPoolableElements() {
    return this.pooledElements;
  }

  /**
   * Convenience method to return Dilutions from this Pool given that the Pooled Elements may well either be a set of
   * single dilutions, or a single plate comprising a number of dilutions within that plate, or something else entirely
   *
   * @return Collection<Dilution> dilutions.
   */
  @Override
  public Collection<? extends Dilution> getDilutions() {
    Set<Dilution> allDilutions = new HashSet<Dilution>();
    for (Poolable poolable : getPoolableElements()) {
      if (poolable instanceof Dilution) {
        allDilutions.add((Dilution)poolable);
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

  public Date getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Double getConcentration() {
    return this.concentration;
  }

  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLabelText() {
    return getAlias();
  }

  public boolean getReadyToRun() {
    return readyToRun;
  }

  public void setReadyToRun(boolean readyToRun) {
    if (!getReadyToRun() && readyToRun) {
      this.readyToRun = readyToRun;
      firePoolReadyEvent();
    }
    else {
      this.readyToRun = readyToRun;
    }
  }

  public void addQc(PoolQC poolQc) throws MalformedPoolQcException {
    this.poolQCs.add(poolQc);
    try {
      poolQc.setPool(this);
    }
    catch (MalformedPoolException e) {
      e.printStackTrace();
    }
  }

  public Collection<PoolQC> getPoolQCs() {
    return poolQCs;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    if (parent.getSecurityProfile().getOwner() != null) {
      setSecurityProfile(parent.getSecurityProfile());
    }
    else {
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
      //PoolEvent pe = new PoolEvent(this, MisoEventType.POOL_READY, "Pool "+getName()+" ("+getAlias()+") ready to run");
      PoolEvent pe = new PoolEvent(this, MisoEventType.POOL_READY, "Pool "+getName()+" ready to run");
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

  /*
  public static String lookupPrefix(PlatformType type) {
    if (type.equals(PlatformType.ILLUMINA)) {
      return "IPO";
    }
    else if (type.equals(PlatformType.SOLID)) {
      return "SPO";
    }
    else if (type.equals(PlatformType.LS454)) {
      return "LPO";
    }
    else if (type.equals(PlatformType.PACBIO)) {
      return "PPO";
    }
    else if (type.equals(PlatformType.IONTORRENT)) {
      return "TPO";
    }
    //must be a universal Pool
    return "UPO";
  }
*/

  public boolean isDeletable() {
    return getId() != AbstractPool.UNSAVED_ID &&
           getPoolableElements().isEmpty();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Platform))
      return false;
    Pool them = (Pool) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == AbstractPool.UNSAVED_ID
        || them.getId() == AbstractPool.UNSAVED_ID) {
      return getPlatformType().equals(them.getPlatformType())
             && getCreationDate().equals(them.getCreationDate());
    }
    else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractPool.UNSAVED_ID) {
      return (int)getId();
    }
    else {
      final int PRIME = 37;
      int hashcode = -1;
      if (getPlatformType() != null) hashcode = PRIME * hashcode + getPlatformType().hashCode();
      if (getCreationDate() != null) hashcode = PRIME * hashcode + getCreationDate().hashCode();
//      if (getIdentificationBarcode() != null) hashcode = PRIME * hashcode + getIdentificationBarcode().hashCode();
//      if (getDilutions() != null && !getDilutions().isEmpty()) hashcode = PRIME * hashcode + getDilutions().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Pool t = (Pool)o;
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
}
