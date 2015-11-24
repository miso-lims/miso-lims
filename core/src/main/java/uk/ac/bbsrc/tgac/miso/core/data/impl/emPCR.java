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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class emPCR implements SecurableByProfile, Comparable, Deletable, Nameable, Serializable {
  public static final Long UNSAVED_ID = 0L;

  public static final String UNITS = "beads/&#181;l";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long pcrId = emPCR.UNSAVED_ID;
  private String pcrCreator;
  private String name;
  private Date creationDate;
  private Double concentration;
  private LibraryDilution libraryDilution;
  private SecurityProfile securityProfile;

  // TODO implement interim pool
  private emPCRPool interimPool;

  private Collection<emPCRDilution> emPCRDilutions = new HashSet<emPCRDilution>();

  /**
   * Construct a new emPCR with a default empty SecurityProfile
   */
  public emPCR() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new emPCR with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public emPCR(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Deprecated
  public Long getPcrId() {
    return pcrId;
  }

  @Deprecated
  public void setPcrId(Long pcrId) {
    this.pcrId = pcrId;
  }

  @Override
  public long getId() {
    return pcrId;
  }

  public void setId(long id) {
    this.pcrId = id;
  }

  public Double getConcentration() {
    return this.concentration;
  }

  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  public LibraryDilution getLibraryDilution() {
    return libraryDilution;
  }

  public void setLibraryDilution(LibraryDilution libraryDilution) {
    this.libraryDilution = libraryDilution;
  }

  public void addEmPcrDilution(emPCRDilution pcrDilution) {
    this.emPCRDilutions.add(pcrDilution);
    pcrDilution.setEmPCR(this);
  }

  public Collection<emPCRDilution> getEmPcrDilutions() {
    return emPCRDilutions;
  }

  public void setEmPcrDilutions(Collection<emPCRDilution> emPCRDilutions) {
    this.emPCRDilutions = emPCRDilutions;
  }

  public String getUnits() {
    return UNITS;
  }

  public String getPcrCreator() {
    return pcrCreator;
  }

  public void setPcrCreator(String pcrCreator) {
    this.pcrCreator = pcrCreator;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public emPCRPool getInterimPool() {
    return interimPool;
  }

  public void setInterimPool(emPCRPool interimPool) {
    this.interimPool = interimPool;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getName());
    sb.append(" : ");
    sb.append(getPcrCreator());
    sb.append(" : ");
    sb.append(getCreationDate());
    sb.append(" : ");
    sb.append(getConcentration() + " " + getUnits());
    return sb.toString();
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public void setSecurityProfile(SecurityProfile profile) {
    this.securityProfile = profile;
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
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  @Override
  public boolean isDeletable() {
    return getId() != emPCR.UNSAVED_ID;
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof emPCR)) return false;
    emPCR them = (emPCR) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == emPCR.UNSAVED_ID || them.getId() == emPCR.UNSAVED_ID) {
      return getName().equals(them.getName()) && getConcentration().equals(them.getConcentration());
    } else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != emPCR.UNSAVED_ID) {
      return (int) getId();
    } else {
      int hashcode = -1;
      if (getName() != null) hashcode = 37 * hashcode + getName().hashCode();
      if (getConcentration() != null) hashcode = 37 * hashcode + getConcentration().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    emPCR t = (emPCR) o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }
}
