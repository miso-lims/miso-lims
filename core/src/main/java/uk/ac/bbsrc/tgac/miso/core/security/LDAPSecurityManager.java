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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;

public class LDAPSecurityManager implements SecurityManager {

  @Autowired
  private UserService userService;

  @Override
  public boolean canCreateNewUser() {
    return false;
  }

  @Override
  public boolean isPasswordMutable() {
    return false;
  }

  @Override
  public void syncUser(UserDetails userDetails) throws IOException {
    User u = LimsSecurityUtils.fromLdapUser(userDetails);
    User dbu = userService.getByLoginName(u.getLoginName());
    if (dbu == null || !dbu.equals(u)) {
      userService.create(u);
    } else {
      LimsSecurityUtils.updateFromLdapUser(dbu, userDetails);
      userService.update(dbu);
    }
  }
}
