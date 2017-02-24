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

/**
 * Provides model access to the underlying MISO LibraryType lookup table. These types should match the SRA submission schema for Library
 * types.
 * <p/>
 * See:
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "LibraryType")
public class LibraryType implements Comparable<LibraryType>, Serializable {

  private static final long serialVersionUID = 1L;

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long libraryTypeId = LibraryType.UNSAVED_ID;

  @Column(nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlatformType platformType;

  @Column(nullable = false)
  private Boolean archived;

  /**
   * Returns the libraryTypeId of this LibraryType object.
   * 
   * @return Long libraryTypeId.
   */
  public Long getId() {
    return libraryTypeId;
  }

  /**
   * Sets the libraryTypeId of this LibraryType object.
   * 
   * @param libraryTypeId
   *          libraryTypeId.
   */
  public void setId(Long libraryTypeId) {
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

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
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
      return getId().intValue();
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
}
