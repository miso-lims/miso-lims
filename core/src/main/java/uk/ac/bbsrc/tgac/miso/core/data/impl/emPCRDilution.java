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
public class emPCRDilution extends AbstractDilution implements Serializable {
  private emPCR emPCR;
  public static final String UNITS = "beads/&#181;l";

  /**
   * Construct a new emPCRDilution with a default empty SecurityProfile
   */
  public emPCRDilution() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new emPCRDilution with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public emPCRDilution(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  public emPCR getEmPCR() {
    return emPCR;
  }

  public void setEmPCR(emPCR emPCR) {
    this.emPCR = emPCR;
  }

  @Override
  public Library getLibrary() {
    if (emPCR != null) {
      return emPCR.getLibraryDilution().getLibrary();
    }
    return null;
  }

  @Override
  public String getUnits() {
    return UNITS;
  }
}
