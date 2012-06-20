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
  public static final Long UNSAVED_ID = null;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long containerId = AbstractSequencerPartitionContainer.UNSAVED_ID;

  private String identificationBarcode;
  private String locationBarcode;
  private Boolean paired = false;

  private Run run = null;

  @OneToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile;
  private PlatformType platformType;
  private String validationBarcode;

  public Long getContainerId() {
    return containerId;
  }

  public void setContainerId(Long containerId) {
    this.containerId = containerId;
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
    return "SPC"+getContainerId();
  }

  public String getLabelText() {
    return getPlatformType().name()+" " + getValidationBarcode();
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

  /**
   * Returns the platformType of this Run object.
   *
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType() {
    return platformType;
  }

  /**
   * Sets the platformType of this Run object.
   *
   * @param platformType PlatformType.
   */
  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
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
    setSecurityProfile(parent.getSecurityProfile());
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
    if (getContainerId() == AbstractSequencerPartitionContainer.UNSAVED_ID
        || them.getContainerId() == AbstractSequencerPartitionContainer.UNSAVED_ID) {
      return getIdentificationBarcode().equals(them.getIdentificationBarcode());
    }
    else {
      return getContainerId().longValue() == them.getContainerId().longValue();
    }
  }

  @Override
  public int hashCode() {
    if (getContainerId() != AbstractSequencerPartitionContainer.UNSAVED_ID) {
      return getContainerId().intValue();
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
    sb.append(getContainerId());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    sb.append(" : ");
    sb.append(getLocationBarcode());
    return sb.toString();
  }

  @Override
  public int compareTo(Object o) {
    SequencerPartitionContainer t = (SequencerPartitionContainer)o;
    if (getContainerId() != null) {
      if (getContainerId() < t.getContainerId()) return -1;
      if (getContainerId() > t.getContainerId()) return 1;
    }
    return 0;
  }
}
