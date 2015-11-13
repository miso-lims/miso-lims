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
 * Provides model access to the underlying MISO LibrarySelectionType lookup table. These types should match the SRA submission schema for
 * Library selection types.
 * <p/>
 * See:
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class LibrarySelectionType implements Comparable, Serializable {
  /** Field UNSAVED_ID */
  public static final Long UNSAVED_ID = 0L;

  /** Field librarySelectionTypeId */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long librarySelectionTypeId = LibrarySelectionType.UNSAVED_ID;
  /** Field name */
  private String name;
  /** Field description */
  private String description;

  /**
   * Returns the librarySelectionTypeId of this LibrarySelectionType object.
   * 
   * @return Long librarySelectionTypeId.
   */
  public Long getLibrarySelectionTypeId() {
    return librarySelectionTypeId;
  }

  /**
   * Sets the librarySelectionTypeId of this LibrarySelectionType object.
   * 
   * @param librarySelectionTypeId
   *          librarySelectionTypeId.
   * 
   */
  public void setLibrarySelectionTypeId(Long librarySelectionTypeId) {
    this.librarySelectionTypeId = librarySelectionTypeId;
  }

  /**
   * Returns the name of this LibrarySelectionType object.
   * 
   * @return String name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this LibrarySelectionType object.
   * 
   * @param name
   *          name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the description of this LibrarySelectionType object.
   * 
   * @return String description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of this LibrarySelectionType object.
   * 
   * @param description
   *          description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof LibrarySelectionType)) return false;
    LibrarySelectionType them = (LibrarySelectionType) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    return getName().equals(them.getName());
  }

  @Override
  public int hashCode() {
    if (getLibrarySelectionTypeId() != UNSAVED_ID) {
      return getLibrarySelectionTypeId().intValue();
    } else {
      int hashcode = -1;
      if (getName() != null) hashcode = 37 * hashcode + getName().hashCode();
      if (getDescription() != null) hashcode = 37 * hashcode + getDescription().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    LibrarySelectionType t = (LibrarySelectionType) o;

    int name = getName().compareTo(t.getName());
    if (name != 0) return name;

    if (getLibrarySelectionTypeId() < t.getLibrarySelectionTypeId()) return -1;
    if (getLibrarySelectionTypeId() > t.getLibrarySelectionTypeId()) return 1;
    return 0;
  }
}
