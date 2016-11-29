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
import java.util.Date;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractDilution;
import uk.ac.bbsrc.tgac.miso.core.data.Library;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class LibraryDilution extends AbstractDilution implements Serializable {

  private static final long serialVersionUID = 1L;
  private Library library;
  private TargetedSequencing targetedSequencing;
  private Date lastModified;
  public static final String UNITS = "nM";

  /**
   * Construct a new LibraryDilution with a default empty SecurityProfile
   */
  public LibraryDilution() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new LibraryDilution with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public LibraryDilution(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public Library getLibrary() {
    return library;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }

  @Override
  public String getUnits() {
    return UNITS;
  }

  public TargetedSequencing getTargetedSequencing() {
    return targetedSequencing;
  }

  public void setTargetedSequencing(TargetedSequencing targetedSequencing) {
    this.targetedSequencing = targetedSequencing;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((library == null) ? 0 : library.hashCode());
    result = prime
        * result
        + ((targetedSequencing == null) ? 0 : targetedSequencing.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    LibraryDilution other = (LibraryDilution) obj;
    if (library == null) {
      if (other.library != null)
        return false;
    }
    else if (!library.equals(other.library))
      return false;
    if (targetedSequencing == null) {
      if (other.targetedSequencing != null)
        return false;
    }
    else if (!targetedSequencing.equals(other.targetedSequencing))
      return false;
    return true;
  }

}
