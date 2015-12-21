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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlateMaterialType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;


/**
 * Skeleton implementation of a Plate
 * 
 * @author Rob Davey
 * @date 05-Sep-2011
 * @since 0.1.1
 */
public abstract class AbstractPlate<T extends List<S>, S extends Plateable> implements Plate<T, S> {
  protected static final Logger log = LoggerFactory.getLogger(AbstractPlate.class);
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long plateId = AbstractPlate.UNSAVED_ID;

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
  private final List<ChangeLog> changeLog = new ArrayList<ChangeLog>();
  private User lastModifier;

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  @Deprecated
  public Long getPlateId() {
    return plateId;
  }

  @Override
  @Deprecated
  public void setPlateId(Long plateId) {
    this.plateId = plateId;
  }

  @Override
  public long getId() {
    return plateId;
  }

  @Override
  public void setId(long id) {
    this.plateId = id;
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
  public abstract void setElements(T elements);

  @Override
  public abstract void addElement(S s);

  @Override
  public abstract Class getElementType();

  @Override
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

  @Override
  public String getLabelText() {
    return getTagBarcode().getSequence() + "(" + getElementType().getSimpleName() + " " + getPlateMaterialType().getKey() + ")";
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
  public boolean isDeletable() {
    return getId() != AbstractPlate.UNSAVED_ID && (getElements() == null || getElements().isEmpty());
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
    if (!(obj instanceof AbstractPlate)) return false;
    AbstractPlate them = (AbstractPlate) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == AbstractPlate.UNSAVED_ID || them.getId() == AbstractPlate.UNSAVED_ID) {
      return getName().equals(them.getName()) && getPlateMaterialType().equals(them.getPlateMaterialType());
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractPlate.UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = -1;
      if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
      if (getPlateMaterialType() != null) hashcode = PRIME * hashcode + getPlateMaterialType().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Plate t = (Plate) o;
    if (getId() != 0L && t.getId() != 0L) {
      if (getId() < t.getId()) return -1;
      if (getId() > t.getId()) return 1;
    } else if (getName() != null && t.getName() != null) {
      return getName().compareTo(t.getName());
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getDescription());
    return sb.toString();
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }
}
