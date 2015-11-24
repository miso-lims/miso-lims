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

package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Provides model access to the underlying MISO LibraryType lookup table. These types should match the SRA submission schema for Library
 * types.
 * <p/>
 * See:
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class LibraryType implements Comparable, Serializable {
  public static final Long UNSAVED_ID = 0L;

  /** Field libraryTypeId */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long libraryTypeId = LibraryType.UNSAVED_ID;
  /** Field description */
  private String description;
  /** Field platformType */
  private String platformType;

  /**
   * Returns the libraryTypeId of this LibraryType object.
   * 
   * @return Long libraryTypeId.
   */
  public Long getLibraryTypeId() {
    return libraryTypeId;
  }

  /**
   * Sets the libraryTypeId of this LibraryType object.
   * 
   * @param libraryTypeId
   *          libraryTypeId.
   */
  public void setLibraryTypeId(Long libraryTypeId) {
    this.libraryTypeId = libraryTypeId;
  }

  /**
   * Returns the description of this LibraryType object.
   * 
   * @return String description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of this LibraryType object.
   * 
   * @param description
   *          description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the platformType of this LibraryType object.
   * 
   * @return String platformType.
   */
  public String getPlatformType() {
    return platformType;
  }

  /**
   * Sets the platformType of this LibraryType object.
   * 
   * @param platformType
   *          platformType.
   */
  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof LibraryType)) return false;
    LibraryType them = (LibraryType) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    return getDescription().equals(them.getDescription());
  }

  @Override
  public int hashCode() {
    if (getLibraryTypeId() != UNSAVED_ID) {
      return getLibraryTypeId().intValue();
    } else {
      int hashcode = -1;
      if (getDescription() != null) hashcode = 37 * hashcode + getDescription().hashCode();
      if (getPlatformType() != null) hashcode = 37 * hashcode + getPlatformType().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    LibraryType t = (LibraryType) o;
    return getDescription().compareTo(t.getDescription());
  }
}
