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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ContainerDerivedInfo;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SequencerPartitionContainerChangeLog;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * Skeleton implementation of a SequencerPartitionContainer
 * 
 * @author Rob Davey
 * @since 0.1.6
 */
@MappedSuperclass
public abstract class AbstractSequencerPartitionContainer<T extends Partition> implements SequencerPartitionContainer<T> {
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long containerId = AbstractSequencerPartitionContainer.UNSAVED_ID;

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
    return getId() != AbstractSequencerPartitionContainer.UNSAVED_ID;
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
  public abstract List<T> getPartitions();

  @Override
  public abstract void setPartitions(List<T> partitions);

  @Override
  public abstract T getPartitionAt(int partitionNumber);

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
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof SequencerPartitionContainer)) return false;
    SequencerPartitionContainer<?> them = (SequencerPartitionContainer<?>) obj;
    // If not saved, then compare resolved actual objects. Otherwise just compare IDs.
    if (getId() == AbstractSequencerPartitionContainer.UNSAVED_ID || them.getId() == AbstractSequencerPartitionContainer.UNSAVED_ID) {
      return getIdentificationBarcode().equals(them.getIdentificationBarcode());
    } else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractSequencerPartitionContainer.UNSAVED_ID) {
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
  public int compareTo(SequencerPartitionContainer<?> t) {
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }
}
