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

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * Skeleton implementation of a Partition
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class AbstractPartition implements Partition {
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id = AbstractPartition.UNSAVED_ID;

  @OneToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile = null;

  private Integer partitionNumber;
  private SequencerPartitionContainer sequencerPartitionContainer = null;

  @Override
  public SequencerPartitionContainer getSequencerPartitionContainer() {
    return this.sequencerPartitionContainer;
  }

  @Override
  public void setSequencerPartitionContainer(SequencerPartitionContainer sequencerPartitionContainer) {
    this.sequencerPartitionContainer = sequencerPartitionContainer;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setPartitionNumber(Integer partitionNumber) {
    this.partitionNumber = partitionNumber;
  }

  @Override
  public Integer getPartitionNumber() {
    return partitionNumber;
  }

  @Override
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  public boolean isDeletable() {
    return getId() != AbstractPartition.UNSAVED_ID;
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

  public abstract void buildSubmission();

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof AbstractPartition)) return false;
    AbstractPartition them = (AbstractPartition) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == AbstractPartition.UNSAVED_ID || them.getId() == AbstractPartition.UNSAVED_ID) {
      return getPartitionNumber().equals(them.getPartitionNumber())
          && getSequencerPartitionContainer().equals(them.getSequencerPartitionContainer());
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractPartition.UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = -1;
      if (getPartitionNumber() != null) hashcode = PRIME * hashcode + getPartitionNumber().hashCode();
      if (getSequencerPartitionContainer() != null) hashcode = PRIME * hashcode + getSequencerPartitionContainer().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Partition t = (Partition) o;
    if (getId() != 0L && t.getId() != 0L) {
      if (getId() < t.getId()) return -1;
      if (getId() > t.getId()) return 1;
    } else {
      if (getPartitionNumber() < t.getPartitionNumber()) return -1;
      if (getPartitionNumber() > t.getPartitionNumber()) return 1;
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getPartitionNumber());
    sb.append(" : ");
    return sb.toString();
  }
}
