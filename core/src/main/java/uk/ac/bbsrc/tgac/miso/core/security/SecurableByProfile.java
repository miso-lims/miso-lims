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

package uk.ac.bbsrc.tgac.miso.core.security;

import com.eaglegenomics.simlims.core.SecurityProfile;

/**
 * Allows an object to be read/write secured by a supplied {@link SecurityProfile}
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface SecurableByProfile {
  /**
   * Returns the securityProfile of this SecurableByProfile object.
   * 
   * @return SecurityProfile securityProfile.
   */
  public SecurityProfile getSecurityProfile();

  /**
   * Sets the securityProfile of this SecurableByProfile object.
   * 
   * @param profile
   *          securityProfile.
   */
  public void setSecurityProfile(SecurityProfile profile);

  /**
   * Inherit the SecurityProfile of the given parent SecurableByProfile
   * 
   * @param parent
   *          of type SecurableByProfile
   * @throws SecurityException
   *           when the parent SecurityProfile could not be inherited
   */
  public default void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    if (parent.getSecurityProfile() != null) {
      setSecurityProfile(parent.getSecurityProfile());
    } else {
      throw new SecurityException("Cannot inherit permissions when parent object owner is not set!");
    }
  }

}
