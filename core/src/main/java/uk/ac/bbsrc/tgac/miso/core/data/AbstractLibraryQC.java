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

import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;

/**
 * Skeleton implementation of a LibraryQC
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class AbstractLibraryQC extends AbstractQC implements LibraryQC {
  public static final String UNITS = "nM";

  private Double results;
  private Integer insertSize;
  private Library library;

  @Override
  public Library getLibrary() {
    return library;
  }

  @Override
  public void setLibrary(Library library) throws MalformedLibraryException {
    this.library = library;
  }

  @Override
  public Double getResults() {
    return results;
  }

  @Override
  public void setResults(Double results) {
    this.results = results;
  }

  @Override
  public Integer getInsertSize() {
    return insertSize;
  }

  @Override
  public void setInsertSize(Integer insertSize) {
    this.insertSize = insertSize;
  }

  /**
   * Equivalency is based on getQcId() if set, otherwise on name
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof AbstractLibraryQC)) return false;
    LibraryQC them = (LibraryQC) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (this.getId() == AbstractQC.UNSAVED_ID || them.getId() == AbstractQC.UNSAVED_ID) {
      return this.getQcCreator().equals(them.getQcCreator()) && this.getQcDate().equals(them.getQcDate())
          && this.getQcType().equals(them.getQcType()) && this.getResults().equals(them.getResults());
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractQC.UNSAVED_ID) {
      return (int) getId();
    } else {
      int hashcode = getQcCreator().hashCode();
      hashcode = 37 * hashcode + getQcDate().hashCode();
      hashcode = 37 * hashcode + getQcType().hashCode();
      hashcode = 37 * hashcode + getResults().hashCode();
      return hashcode;
    }
  }
}