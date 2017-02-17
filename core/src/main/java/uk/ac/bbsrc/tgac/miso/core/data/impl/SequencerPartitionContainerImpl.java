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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AutoPopulatingList;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SequencerPartitionContainerChangeLog;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 14-May-2012
 * @since 0.1.6
 */
@Entity
@Table(name = "SequencerPartitionContainer")
public class SequencerPartitionContainerImpl implements SequencerPartitionContainer, Serializable {

  private static final long serialVersionUID = 1L;
  public static final Long UNSAVED_ID = 0L;

  private static final Logger log = LoggerFactory.getLogger(SequencerPartitionContainerImpl.class);

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long containerId = SequencerPartitionContainerImpl.UNSAVED_ID;

  // identificationBarcode is displayed as "serial number" to the user
  private String identificationBarcode;
  private String locationBarcode;

  @ManyToMany(targetEntity = RunImpl.class)
  @JoinTable(name = "Run_SequencerPartitionContainer", joinColumns = {
      @JoinColumn(name = "containers_containerId") }, inverseJoinColumns = {
          @JoinColumn(name = "Run_runId") })
  private Collection<Run> runs = null;

  @ManyToOne(targetEntity = SecurityProfile.class, cascade = CascadeType.ALL)
  @JoinColumn(name = "securityProfile_profileId")
  private SecurityProfile securityProfile;

  @ManyToOne(targetEntity = PlatformImpl.class)
  @JoinColumn(name = "platform")
  private Platform platform;

  private String validationBarcode;

  @OneToMany(targetEntity = SequencerPartitionContainerChangeLog.class, mappedBy = "sequencerPartitionContainer")
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  @OneToOne(targetEntity = ContainerDerivedInfo.class)
  @PrimaryKeyJoinColumn
  private ContainerDerivedInfo derivedInfo;

  @OneToMany(targetEntity = PartitionImpl.class, cascade = CascadeType.ALL)
  @JoinTable(name = "SequencerPartitionContainer_Partition", joinColumns = {
      @JoinColumn(name = "container_containerId", updatable = false) }, inverseJoinColumns = {
          @JoinColumn(name = "partitions_partitionId", updatable = false) })
  @JsonIgnore
  private List<Partition> partitions = new AutoPopulatingList<>(PartitionImpl.class);

  @Transient
  private int partitionLimit = 8;

  /**
   * Construct a new SequencerPartitionContainer with a default empty SecurityProfile
   */
  public SequencerPartitionContainerImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new SequencerPartitionContainer with a SecurityProfile owned by the given User
   * 
   * @param user of type User
   */
  public SequencerPartitionContainerImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
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
    return (derivedInfo == null ? null : derivedInfo.getLastModified());
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public long getId() {
    return containerId;
  }

  @Override
  public void setId(long id) {
    this.containerId = id;
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  @Override
  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  @Override
  public String getLabelText() {
    return getPlatform().getPlatformType().name() + " " + getValidationBarcode();
  }

  @Override
  public boolean isDeletable() {
    return getId() != SequencerPartitionContainerImpl.UNSAVED_ID;
  }

  @Override
  public String getValidationBarcode() {
    return validationBarcode;
  }

  @Override
  public void setValidationBarcode(String validationBarcode) {
    this.validationBarcode = validationBarcode;
  }

  /**
   * Containers don't have names, but they implement an interface which requires this method.
   */
  @Override
  public String getName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Run> getRuns() {
    return runs;
  }

  @Override
  public void setRuns(Collection<Run> runs) {
    this.runs = runs;
  }

  @Override
  public Run getLastRun() {
    Run lastRun = null;
    for (Run thisRun : getRuns()) {
      if (lastRun == null) {
        lastRun = thisRun;
      } else if (lastRun.getStatus().getStartDate() == null && thisRun.getStatus().getStartDate() == null) {
        if (thisRun.getLastUpdated().after(lastRun.getLastUpdated())) lastRun = thisRun;
      } else if (lastRun.getStatus().getStartDate() == null && thisRun.getStatus().getStartDate() != null) {
        lastRun = thisRun;
      } else if (lastRun.getStatus().getStartDate() != null && thisRun.getStatus().getStartDate() == null) {
        continue;
      } else if (thisRun.getStatus().getStartDate().after(lastRun.getStatus().getStartDate())) {
        lastRun = thisRun;
      }
    }
    return lastRun;
  }

  @Override
  public void setRun(Run run) {
    if (run != null && runs.size() > 1) {
      throw new IllegalArgumentException("Cannot set single run on a container with multiple runs already linked!");
    } else {
      runs = new ArrayList<>();
      if (run != null) runs.add(run);
    }
  }

  @Override
  public Platform getPlatform() {
    return platform;
  }

  @Override
  public void setPlatform(Platform platform) {
    this.platform = platform;
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
  public List<Partition> getPartitions() {
    return partitions;
  }

  @Override
  public void setPartitions(List<Partition> partitions) {
    this.partitions = partitions;
  }

  @Override
  public Partition getPartitionAt(int partitionNumber) throws IndexOutOfBoundsException {
    return partitions.get(partitionNumber - 1);
  }

  @Override
  public void setPartitionLimit(int partitionLimit) {
    this.partitionLimit = partitionLimit;
  }

  @Override
  public void initEmptyPartitions() {
    getPartitions().clear();
    for (int i = 0; i < partitionLimit; i++) {
      addNewPartition();
    }
  }

  @Override
  public void addNewPartition() {
    if (getPartitions().size() < partitionLimit) {
      PartitionImpl partition = new PartitionImpl();
      partition.setSequencerPartitionContainer(this);
      partition.setPartitionNumber(getPartitions().size() + 1);
      partition.setSecurityProfile(getSecurityProfile());
      getPartitions().add(partition);
    } else {
      log.warn("This sequencing container is limited to " + partitionLimit + " lanes");
    }
  }

  public void addPartition(Partition partition) {
    if (getPartitions().size() < partitionLimit) {
      if (!getPartitions().contains(partition)) {
        if (partition.getSequencerPartitionContainer() == null) partition.setSequencerPartitionContainer(this);
        if (partition.getPartitionNumber() == null) partition.setPartitionNumber(getPartitions().size() + 1);
        if (partition.getSecurityProfile() == null) partition.setSecurityProfile(getSecurityProfile());
        getPartitions().add(partition);
      } else {
        log.warn("This sequencing container already contains that lane");
      }
    } else {
      log.warn("This sequencing container is limited to " + partitionLimit + " lanes");
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof SequencerPartitionContainer)) return false;
    SequencerPartitionContainer them = (SequencerPartitionContainer) obj;
    // If not saved, then compare resolved actual objects. Otherwise just compare IDs.
    if (getId() == SequencerPartitionContainerImpl.UNSAVED_ID || them.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
      return getIdentificationBarcode().equals(them.getIdentificationBarcode());
    } else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != SequencerPartitionContainerImpl.UNSAVED_ID) {
      return (int) getId();
    } else {
      int hashcode = -1;
      if (getIdentificationBarcode() != null) hashcode = 37 * hashcode + getIdentificationBarcode().hashCode();
      return hashcode;
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    sb.append(" : ");
    sb.append(getLocationBarcode());
    return sb.toString();
  }

  @Override
  public int compareTo(SequencerPartitionContainer t) {
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    SequencerPartitionContainerChangeLog changeLog = new SequencerPartitionContainerChangeLog();
    changeLog.setSequencerPartitionContainer(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }
}
