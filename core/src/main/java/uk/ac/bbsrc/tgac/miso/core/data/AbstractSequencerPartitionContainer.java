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
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import javax.persistence.*;
import java.util.List;

/**
 * Skeleton implementation of a SequencerPartitionContainer
 *
 * @author Rob Davey
 * @since 0.1.6
 */
public abstract class AbstractSequencerPartitionContainer<T extends Partition> implements SequencerPartitionContainer<T> {
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long containerId = AbstractSequencerPartitionContainer.UNSAVED_ID;

  private String identificationBarcode;
  private String locationBarcode;
  private Boolean paired = false;
  private String name;
  private Run run = null;

  @OneToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile;
  private Platform platform;
  private String validationBarcode;

  @Deprecated
  public Long getContainerId() {
    return containerId;
  }

  @Deprecated
  public void setContainerId(Long containerId) {
    this.containerId = containerId;
  }

  @Override
  public long getId() {
    return containerId;
  }

  public void setId(long id) {
    this.containerId = id;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabelText() {
    return getPlatform().getPlatformType().name()+" " + getValidationBarcode();
  }

  public boolean isDeletable() {
    return getId() != AbstractSequencerPartitionContainer.UNSAVED_ID;
  }

  public String getValidationBarcode() {
    return validationBarcode;
  }

  public void setValidationBarcode(String validationBarcode) {
    this.validationBarcode = validationBarcode;
  }

  public Boolean getPaired() {
    return paired;
  }

  public void setPaired(Boolean paired) {
    this.paired = paired;
  }

  public abstract List<T> getPartitions();

  public abstract void setPartitions(List<T> partitions);

  public abstract T getPartitionAt(int partitionNumber);

  public Run getRun() {
    return run;
  }

  public void setRun(Run run) {
    this.run = run;
  }

  @Override
  public Platform getPlatform() {
    return platform;
  }

  @Override
  public void setPlatform(Platform platform) {
    this.platform = platform;
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
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof SequencerPartitionContainer))
      return false;
    SequencerPartitionContainer them = (SequencerPartitionContainer) obj;
    // If not saved, then compare resolved actual objects. Otherwise just compare IDs.
    if (getId() == AbstractSequencerPartitionContainer.UNSAVED_ID
        || them.getId() == AbstractSequencerPartitionContainer.UNSAVED_ID) {
      return getIdentificationBarcode().equals(them.getIdentificationBarcode());
    }
    else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractSequencerPartitionContainer.UNSAVED_ID) {
      return (int)getId();
    }
    else {
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
  public int compareTo(Object o) {
    SequencerPartitionContainer t = (SequencerPartitionContainer)o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }
}
