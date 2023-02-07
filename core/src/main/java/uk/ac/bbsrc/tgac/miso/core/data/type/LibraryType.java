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

package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

@Entity
@Table(name = "LibraryType")
public class LibraryType implements Comparable<LibraryType>, Deletable, Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long libraryTypeId = LibraryType.UNSAVED_ID;

  @Column(nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlatformType platformType;

  @Column(nullable = false)
  private boolean archived = false;

  private String abbreviation;

  /**
   * Returns the libraryTypeId of this LibraryType object.
   * 
   * @return Long libraryTypeId.
   */
  @Override
  public long getId() {
    return libraryTypeId;
  }

  /**
   * Sets the libraryTypeId of this LibraryType object.
   * 
   * @param libraryTypeId
   *          libraryTypeId.
   */
  @Override
  public void setId(long libraryTypeId) {
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
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType() {
    return platformType;
  }

  /**
   * Sets the platformType of this LibraryType object.
   * 
   * @param platformType
   *          platformType.
   */
  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public boolean getArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
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
    if (getId() != UNSAVED_ID) {
      return Long.valueOf(getId()).intValue();
    } else {
      int hashcode = -1;
      if (getDescription() != null) hashcode = 37 * hashcode + getDescription().hashCode();
      if (getPlatformType() != null) hashcode = 37 * hashcode + getPlatformType().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(LibraryType t) {
    return getDescription().compareTo(t.getDescription());
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Library Type";
  }

  @Override
  public String getDeleteDescription() {
    return getDescription() + " (" + getPlatformType().getKey() + ")";
  }

}
