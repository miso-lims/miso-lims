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

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;

import java.io.Serializable;

/**
 * Concrete implementation of a LibraryQC
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class LibraryQCImpl extends AbstractLibraryQC implements Serializable {
  /**
   * Construct a new LibraryQC
   */
  public LibraryQCImpl() {
  }

  /**
   * Construct a new LibraryQC from a parent Library, checking that the given User can read that Library
   * 
   * @param library
   *          of type Library
   * @param user
   *          of type User
   */
  public LibraryQCImpl(Library library, User user) {
    if (library.userCanRead(user)) {
      try {
        setLibrary(library);
      } catch (MalformedLibraryException e) {
        e.printStackTrace();
      }
    } else {
    }
  }

  public boolean userCanRead(User user) {
    return true;
  }

  public boolean userCanWrite(User user) {
    return true;
  }
}
