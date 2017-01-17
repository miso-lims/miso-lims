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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.ExperimentChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

/**
 * Concrete implementation of an Experiment
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Experiment")
public class ExperimentImpl implements Experiment, Serializable {
  public static final Long UNSAVED_ID = 0L;

  private String accession;

  private String alias;

  @OneToMany(targetEntity = ExperimentChangeLog.class, mappedBy = "experiment")
  private final List<ChangeLog> changeLog = new ArrayList<>();

  private String description;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long experimentId = UNSAVED_ID;

  @ManyToMany(targetEntity = KitImpl.class)
  @JoinTable(name = "Experiment_Kit", inverseJoinColumns = { @JoinColumn(name = "kits_kitId") }, joinColumns = {
      @JoinColumn(name = "experiments_experimentId") })
  private Collection<Kit> kits = new HashSet<>();

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn
  private User lastModifier;

  @Column(nullable = false)
  private String name;

  @ManyToOne(targetEntity = PlatformImpl.class)
  @JoinColumn(name = "platform_platformId")
  private Platform platform;

  // defines a pool on which this experiment will operate. This contains one or more dilutions of a sample
  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinTable(name = "Pool_Experiment", inverseJoinColumns = { @JoinColumn(name = "pool_poolId") }, joinColumns = {
      @JoinColumn(name = "experiments_experimentId") })
  private Pool pool;

  // defines the parent run which processes this experiment
  @ManyToOne(targetEntity = RunImpl.class)
  @JoinTable(name = "Experiment_Run", joinColumns = { @JoinColumn(name = "runs_runId") }, inverseJoinColumns = {
      @JoinColumn(name = "Experiment_experimentId") })
  private Run run;

  @ManyToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile = new SecurityProfile();

  @ManyToOne(targetEntity = StudyImpl.class)
  @JoinColumn(name = "study_studyId")
  private Study study = null;
  @Column(nullable = false)
  private String title;

  /**
   * Construct a new Experiment with a default empty SecurityProfile
   */
  public ExperimentImpl() {
  }

  /**
   * If the given User can read the parent Study, construct a new Experiment with a SecurityProfile inherited from the parent Study. If not,
   * construct a new Experiment with a SecurityProfile owned by the given User
   * 
   * @param study
   *          of type Study
   * @param user
   *          of type User
   */
  public ExperimentImpl(Study study, User user) {
    if (study.userCanRead(user)) {
      setStudy(study);
      setSecurityProfile(study.getSecurityProfile());
    } else {
      setSecurityProfile(new SecurityProfile(user));
    }
  }

  /**
   * Construct a new Experiment with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public ExperimentImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @CoverageIgnore
  @Override
  public void addKit(Kit kit) {
    this.kits.add(kit);
  }

  @CoverageIgnore
  @Override
  public int compareTo(Object o) {
    final Experiment t = (Experiment) o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
   */
  @CoverageIgnore
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Experiment)) return false;
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

  @Override
  public String getAccession() {
    return accession;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public List<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public long getId() {
    return experimentId;
  }

  @Override
  public Collection<Kit> getKits() {
    return kits;
  }

  @Override
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

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Platform getPlatform() {
    return platform;
  }

  @Override
  public Pool getPool() {
    return pool;
  }

  @Override
  public Run getRun() {
    return run;
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public Study getStudy() {
    return study;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @CoverageIgnore
  @Override
  public int hashCode() {
    if (getId() != UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
      if (getAlias() != null) hashcode = 37 * hashcode + getAlias().hashCode();
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

  @CoverageIgnore
  @Override
  public boolean isDeletable() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public void setAccession(String accession) {
    this.accession = accession;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setId(long id) {
    this.experimentId = id;
  }

  @Override
  public void setKits(Collection<Kit> kits) {
    this.kits = kits;
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
  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  @Override
  public void setPool(Pool pool) {
    this.pool = pool;
  }

  @Override
  public void setRun(Run run) {
    this.run = run;
  }

  @Override
  public void setSecurityProfile(SecurityProfile profile) {
    this.securityProfile = profile;
  }

  @Override
  public void setStudy(Study study) {
    this.study = study;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @CoverageIgnore
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getAccession());
    sb.append(" : ");
    sb.append(getTitle());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getDescription());
    sb.append(" : ");
    sb.append(getPool());
    sb.append(" : ");
    if (getPlatform() != null) {
      sb.append(getPlatform().getInstrumentModel());
      sb.append(" : ");
    }

    return sb.toString();
  }

  @CoverageIgnore
  @Override
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @CoverageIgnore
  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }
}
