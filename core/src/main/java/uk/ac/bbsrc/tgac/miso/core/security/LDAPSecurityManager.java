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

package uk.ac.bbsrc.tgac.miso.core.security;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsManager;

import java.io.IOException;

/**
 * Extension of the basic LocalSecurityManager, this class adds the ability to save users initially authorised by LDAP into the MISO DB so that
 * the security information can be utilised by the SecurityProfile system
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class LDAPSecurityManager extends LocalSecurityManager {
  /** Field log  */
  protected static final Logger log = LoggerFactory.getLogger(LDAPSecurityManager.class);

  @Autowired
  private LdapUserDetailsManager ldapUserManager;

  @Autowired
  private String groupRoleAttributeName;

  @Autowired
  private String groupSearchBase;

  @Autowired
  private String groupMemberAttributeName;

  public void setLdapUserManager(LdapUserDetailsManager ldapUserManager) {
    this.ldapUserManager = ldapUserManager;
  }

  public void setGroupRoleAttributeName(String attribute) {
    this.groupRoleAttributeName = attribute;
  }

  public void setGroupSearchBase(String base) {
    this.groupSearchBase = base;
  }

  public void setGroupMemberAttributeName(String attribute) {
    this.groupMemberAttributeName = attribute;
  }

  public String getGroupRoleAttributeName() {
    return this.groupRoleAttributeName;
  }

  public String getGroupSearchBase() {
    return this.groupSearchBase;
  }

  public String getGroupMemberAttributeName() {
    return this.groupMemberAttributeName;
  }

  /**
   * Saves the User to the MISO database
   *
   * @param user of type User
   * @throws IOException when the User cannot be saved
   */
  public long saveUser(User user) throws IOException {
    //set group search parameters
    //ldapUserManager.setGroupRoleAttributeName(getGroupRoleAttributeName());
    //ldapUserManager.setGroupSearchBase(getGroupSearchBase());
    //ldapUserManager.setGroupMemberAttributeName(getGroupMemberAttributeName());

/*    if (!ldapUserManager.userExists(user.getLoginName())) {
      //by default, all users that register via the LIMS will be ROLE_EXTERNAL and active
      user.setActive(true);
      user.setExternal(true);
      user.setRoles(new String[]{"ROLE_EXTERNAL"});

      //do LDAP storage
      log.info("Adding " + LimsSecurityUtils.toLdapUser(user) + " to LDAP server");
      ldapUserManager.createUser(LimsSecurityUtils.toLdapUser(user));
    }
    else {
    */
      User existingUser = super.getUserByLoginName(user.getLoginName());
      if (existingUser != null) {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
          //this should be the case if the user has already logged in and is wishing to update details, rather than create

          //TODO - this seems to change the LDAP password (an encrypted version of the already encrypted string)
          //ldapUserManager.updateUser(user.toLdapUser());

          if (user.getUserId() == null) {
            //this will happen if the user auths against LDAP and the user exists in the DB
            //i.e. when they log into the LIMS for the very first time
            user.setUserId(existingUser.getUserId());
          }

          if ("".equals(user.getFullName()) || user.getFullName() == null) {
            throw new IOException("Cannot save user with no full name / display name.");
          }

          if ("".equals(user.getEmail()) || user.getEmail() == null) {
            throw new IOException("Cannot save user with no email.");
          }

          if ("".equals(user.getLoginName()) || user.getLoginName() == null) {
            throw new IOException("Cannot save user with no login name.");
          }

          if ("".equals(user.getPassword()) || user.getPassword() == null) {
            throw new IOException("Cannot save user with no password.");
          }

          return super.saveUser(user);
        }
        else {
          //probably attempting registration, i.e. no auth session and user exists in LDAP and LIMS
          throw new IOException("User with supplied login name already exists");
        }
      }
      else {
        log.info("Creating " + user.getLoginName() + " in LIMS");
        return super.saveUser(user);
      }
    }
  //}
}