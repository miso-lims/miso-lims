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

package uk.ac.bbsrc.tgac.miso.core.security.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.InetOrgPerson;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.security.MisoAuthority;

/**
 * Helper class that provides various methods to deal with security authorisation and profiles
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class LimsSecurityUtils {
  /** Field log */
  protected static final Logger log = LoggerFactory.getLogger(LimsSecurityUtils.class);

  /** Prefix to be removed from each role. */
  private static String rolePrefix = "";

  @Value("${security.ldap.stripRolePrefix}")
  public void setRolePrefix(String rolePrefix) {
    LimsSecurityUtils.rolePrefix = rolePrefix;
  }

  /**
   * Converts a LDAP {@link org.springframework.security.core.userdetails.UserDetails} implementation into a MISO user object
   * 
   * @param details
   *          of type implementing UserDetails
   * @return User
   */
  public static User fromLdapUser(UserDetails details) {
    // remember that this user has no userID!
    // upon persistence using the default MISO securityManager, this user is checked against the MISO SQL DB
    // by username. If a user already exists with that username that has been authed, then they must be the same
    // user and this user will inherit the already-persisted userID.
    final UserImpl user = new UserImpl();

    updateFromLdapUser(user, details);
    user.setLoginName(details.getUsername().toLowerCase());

    return user;
  }

  public static void updateFromLdapUser(User target, UserDetails ldapUserDetails) {
    final List<String> roles = new ArrayList<>();
    for (final GrantedAuthority ga : ldapUserDetails.getAuthorities()) {
      roles.add(removePrefix(ga.toString(), LimsSecurityUtils.rolePrefix));
    }
    target.setRoles(roles.toArray(new String[0]));

    target.setActive(ldapUserDetails.isAccountNonExpired());
    target.setAdmin(roles.contains(MisoAuthority.ROLE_ADMIN.name()));
    target.setInternal(roles.contains(MisoAuthority.ROLE_INTERNAL.name()));

    target.setPassword(ldapUserDetails.getPassword());
    if(ldapUserDetails instanceof InetOrgPerson) {
      InetOrgPerson person = (InetOrgPerson) ldapUserDetails;
      target.setFullName(person.getDisplayName());
      target.setEmail(person.getMail());
    } else {
      throw new IllegalArgumentException(
          "UserDetails is not an InetOrgPerson. Check AuthenticationProvider/UserDetailsContextMapper config");
    }
  }

  private static String removePrefix(String s, String prefix) {
    String result = s;
    if (s.startsWith(prefix)) {
      result = s.substring(prefix.length());
    }
    return result;
  }

  public static org.springframework.security.core.userdetails.User toUserDetails(User user) {
    final Collection<GrantedAuthority> auths = user.getPermissionsAsAuthorities();
    return new org.springframework.security.core.userdetails.User(user.getLoginName(),
        user.getPassword() == null ? "junkToShutUpSpring" : user.getPassword(), user.isActive(), user.isActive(),
        user.isActive(), user.isActive(), auths);
  }

}
