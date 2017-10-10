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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
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

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.ExperimentChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

/**
 * An Experiment contains design information about a sequencing experiment, as part of a parent {@link Study}.
 */
@Entity
@Table(name = "Experiment")
public class Experiment implements SecurableByProfile, Comparable<Experiment>, Deletable, Nameable, ChangeLoggable, Serializable {
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
  }

  /** Field PREFIX */
  public static final String PREFIX = "EXP";

  private static final long serialVersionUID = 1L;

  /** Field UNSAVED_ID */
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
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  // defines a library on which this experiment will operate.
  @ManyToOne(targetEntity = LibraryImpl.class)
  @JoinColumn(name = "library_libraryId")
  private Library library;

  @Column(nullable = false)
  private String name;

  @ManyToOne(targetEntity = Platform.class)
  @JoinColumn(name = "platform_platformId")
  private Platform platform;

  // defines the parent run which processes this experiment
  @OneToMany(mappedBy = "experiment", cascade=CascadeType.ALL)
  private List<RunPartition> runPartitions;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "securityProfile_profileId")
  private SecurityProfile securityProfile = new SecurityProfile();

  @ManyToOne(targetEntity = StudyImpl.class)
  @JoinColumn(name = "study_studyId")
  private Study study;
  @Column(nullable = false)
  private String title;

  /**
   * Construct a new Experiment with a default empty SecurityProfile
   */
  public Experiment() {
  }

  @CoverageIgnore
  public void addKit(Kit kit) {
    this.kits.add(kit);
  }

  @Override
  @CoverageIgnore
  public int compareTo(Experiment t) {
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
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

  public User getLastModifier() {
    return lastModifier;
  }

  public Library getLibrary() {
    return library;
  }

  @Override
  public String getName() {
    return name;
  }

  public Platform getPlatform() {
    return platform;
  }

  public List<RunPartition> getRunPartitions() {
    return runPartitions;
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
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

  @Override
  @CoverageIgnore
  public boolean isDeletable() {
    return getId() != UNSAVED_ID;
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

  public void setId(long id) {
    this.experimentId = id;
  }

  public void setKits(Collection<Kit> kits) {
    this.kits = kits;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  public void setRunPartitions(List<RunPartition> runPartitions) {
    this.runPartitions = runPartitions;
  }

  @Override
  public void setSecurityProfile(SecurityProfile profile) {
    this.securityProfile = profile;
  }

  public void setStudy(Study study) {
    this.study = study;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  @CoverageIgnore
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @Override
  @CoverageIgnore

  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }
}
