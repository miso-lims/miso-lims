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
import uk.ac.bbsrc.tgac.miso.core.data.type.PlateMaterialType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import javax.persistence.*;
import java.util.*;

/**
 * Skeleton implementation of a Plate
 *
 * @author Rob Davey
 * @date 05-Sep-2011
 * @since 0.1.1
 */
public abstract class AbstractPlate<T extends LinkedList<S>, S> implements Plate<T, S> {
  public static final Long UNSAVED_ID = null;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long plateId = AbstractPlate.UNSAVED_ID;

  private SecurityProfile securityProfile;
  private String name;
  private String description;
  private Date creationDate;

  @Enumerated(EnumType.STRING)
  private PlateMaterialType plateMaterialType;

  private TagBarcode tagBarcode;
  private String identificationBarcode;
  private String locationBarcode;

  private Date lastUpdated;

  @Override
  public Long getPlateId() {
    return plateId;
  }

  @Override
  public void setPlateId(Long plateId) {
    this.plateId = plateId;
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
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public PlateMaterialType getPlateMaterialType() {
    return plateMaterialType;
  }

  @Override
  public void setPlateMaterialType(PlateMaterialType plateMaterialType) {
    this.plateMaterialType = plateMaterialType;
  }

  @Override
  public TagBarcode getTagBarcode() {
    return tagBarcode;
  }

  @Override
  public void setTagBarcode(TagBarcode tagBarcode) {
    this.tagBarcode = tagBarcode;
  }

  @Override
  public abstract int getSize();

  @Override
  public abstract T getElements();

  @Override
  public abstract void addElement(S s);

  @Override
  public abstract Class getElementType();

  public abstract Collection<S> getInternalPoolableElements();

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

  public String getLabelText() {
    return getTagBarcode().getSequence() + "("+getElementType().getSimpleName() + " " + getPlateMaterialType().getKey()+")";
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public boolean isDeletable() {
    return getPlateId() != AbstractPlate.UNSAVED_ID &&
           getElements().isEmpty();
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
    setSecurityProfile(parent.getSecurityProfile());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof AbstractPlate))
      return false;
    AbstractPlate them = (AbstractPlate) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getPlateId() == AbstractPlate.UNSAVED_ID
        || them.getPlateId() == AbstractPlate.UNSAVED_ID) {
      return getName().equals(them.getName()) &&
             getPlateMaterialType().equals(them.getPlateMaterialType());
    }
    else {
      return this.getPlateId().longValue() == them.getPlateId().longValue();
    }
  }

  @Override
  public int hashCode() {
    if (getPlateId() != AbstractPlate.UNSAVED_ID) {
      return getPlateId().intValue();
    }
    else {
      final int PRIME = 37;
      int hashcode = -1;
      if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
      if (getPlateMaterialType() != null) hashcode = PRIME * hashcode + getPlateMaterialType().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Plate t = (Plate)o;
    if (getPlateId() != null && t.getPlateId() != null) {
      if (getPlateId() < t.getPlateId()) return -1;
      if (getPlateId() > t.getPlateId()) return 1;
    }
    else if (getName() != null && t.getName() != null) {
      return getName().compareTo(t.getName());
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getPlateId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getDescription());
    return sb.toString();
  }  
}
