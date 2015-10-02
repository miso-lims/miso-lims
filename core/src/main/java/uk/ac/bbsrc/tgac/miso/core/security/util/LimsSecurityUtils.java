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

package uk.ac.bbsrc.tgac.miso.core.security.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * Helper class that provides various methods to deal with security authorisation and profiles
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class LimsSecurityUtils {
   /** Field log */
   protected static final Logger log = LoggerFactory.getLogger(LimsSecurityUtils.class);

   /**
    * Converts a LDAP {@link org.springframework.security.ldap.userdetails.InetOrgPerson} object into a MISO user object
    *
    * @param details
    *           of type InetOrgPerson
    * @return User
    */
   public static User fromLdapUser(InetOrgPerson details) {
      // remember that this user has no userID!
      // upon persistence using the default MISO securityManager, this user is checked against the MISO SQL DB
      // by username. If a user already exists with that username that has been authed, then they must be the same
      // user and this user will inherit the already-persisted userID.
      final UserImpl user = new UserImpl();

      user.setActive(details.isAccountNonExpired());
      user.setAdmin(details.getAuthorities().contains(new GrantedAuthorityImpl("ROLE_ADMIN")));
      user.setExternal(details.getAuthorities().contains(new GrantedAuthorityImpl("ROLE_EXTERNAL")));
      user.setInternal(details.getAuthorities().contains(new GrantedAuthorityImpl("ROLE_INTERNAL")));
      user.setLoginName(details.getUsername().toLowerCase());
      user.setPassword(details.getPassword());
      user.setFullName(details.getDisplayName());
      user.setEmail(details.getMail());

      final List<String> roles = new ArrayList<String>();
      for (final GrantedAuthority ga : details.getAuthorities()) {
         roles.add(ga.toString());
      }
      user.setRoles(roles.toArray(new String[0]));

      return user;
   }

   /**
    * Converts a MISO User into a LDAP {@link org.springframework.security.ldap.userdetails.LdapUserDetails} object
    *
    * @param user
    *           of type User
    * @return LdapUserDetails
    */
   public static LdapUserDetails toLdapUser(User user) {
      final InetOrgPerson.Essence p = new org.springframework.security.ldap.userdetails.InetOrgPerson.Essence();
      p.setEnabled(user.isActive());
      p.setAccountNonExpired(user.isActive());
      p.setAccountNonLocked(user.isActive());

      final Collection<GrantedAuthority> auths = user.getPermissionsAsAuthorities();
      // TODO we don't currently store LIMS activity roles in LDAP
      // auths.addAll(getRolesAsAuthorities());
      p.setAuthorities(auths);

      p.setDisplayName(user.getFullName());
      p.setMail(user.getEmail());

      p.setUsername(user.getLoginName());
      p.setPassword(user.getPassword());
      p.setUid(user.getLoginName());

      // must set these as part of the Person creation assertions
      p.setSn(user.getFullName().split(" ")[1]);
      p.setCn(new String[] { user.getFullName() });

      final DistinguishedName newDn = new DistinguishedName();
      newDn.add("ou", "Users");
      newDn.add("cn", user.getFullName());
      p.setDn(newDn);

      return p.createUserDetails();
   }

   public static org.springframework.security.core.userdetails.User toUserDetails(User user) {
      final Collection<GrantedAuthority> auths = user.getPermissionsAsAuthorities();
      return new org.springframework.security.core.userdetails.User(user.getLoginName(), user.getPassword(), user.isActive(),
            user.isActive(), user.isActive(), user.isActive(), auths);
   }

   /**
    * From a subset of supplied users (usually the list of all users in MISO), returns a collection of Users that can be set as owners of
    * the supplied SecurableByProfile object
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @param allUsers
    *           of type Collection<User>
    * @return Set<User> the collection of Users that can own the given SecurableByProfile object
    * @throws IOException
    *            when
    */
   public static Set<User> getPotentialOwners(User user, SecurableByProfile object, Collection<User> allUsers) throws IOException {
      final SortedSet<User> owners = new TreeSet<User>(new FullNameComparator());
      if (user.isAdmin()) {
         for (final User u : allUsers) {
            owners.add(u);
         }
         return owners;
      } else {
         if (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user)) {
            for (final User u : allUsers) {
               if (!u.isAdmin()) {
                  owners.add(u);
               }
            }
            return owners;
         }
      }
      return Collections.emptySet();
   }

   /**
    * From a subset of supplied users (usually the list of all users in MISO), returns a collection of Users that can be set as owners of
    * the supplied SecurableByProfile object
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @param allUsers
    *           of type Collection<User>
    * @return Set<User> the collection of Users that can own the given SecurableByProfile object
    */
   public static Set<User> getAccessibleUsers(User user, SecurableByProfile object, Collection<User> allUsers) {
      final SortedSet<User> su = new TreeSet<User>(new FullNameComparator());

      if (user.isAdmin()) {
         for (final User u : allUsers) {
            su.add(u);
         }
         return su;
      } else if (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user)) {
         for (final User u : allUsers) {
            if (!u.isAdmin()) {
               su.add(u);
            }
         }
         return su;
      }
      return Collections.emptySet();
   }

   /**
    * Gets the list of users that have been allowed to read the given SecurableByProfile object. The supplied User acts as a basis for the
    * Users that are shown in the returned list. The following rules apply:
    * <p/>
    * If the supplied basis User is an admin, all users are shown.<br/>
    * If the basis User isn't an admin, but owns the object then all Users apart from admins are shown.<br/>
    * If the basis User isn't an admin, and doesn't own the object, then no Users will be returned.
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @return Set<User>
    */
   public static Set<User> getSelectedReadUsers(User user, SecurableByProfile object) {
      final SortedSet<User> su = new TreeSet<User>(new FullNameComparator());
      if (user.isAdmin()) {
         return new HashSet<User>(object.getSecurityProfile().getReadUsers());
      } else if (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user)) {
         for (final User u : object.getSecurityProfile().getReadUsers()) {
            if (!u.isAdmin()) {
               su.add(u);
            }
         }
         return su;
      }
      return Collections.emptySet();
   }

   /**
    * Gets the list of users that can be set to be allowed to read SecurableByProfile object. The supplied User acts as a basis for the
    * Users that are shown in the returned list. The following rules apply:
    * <p/>
    * If the supplied basis User is an admin, all users that can't already read the SecurableByProfile object are shown.<br/>
    * If the basis User isn't an admin, but owns the object, then all Users apart from admins that can't already read the SecurableByProfile
    * object are shown.<br/>
    * If the basis User isn't an admin, and doesn't own the object, then no Users will be returned.
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @param allUsers
    *           of type Collection<User>
    * @return Set<User>
    */
   public static Set<User> getAvailableReadUsers(User user, SecurableByProfile object, Collection<User> allUsers) {
      final SortedSet<User> su = new TreeSet<User>(new FullNameComparator());
      if (user.isAdmin()) {
         for (final User u : allUsers) {
            if (!object.getSecurityProfile().getReadUsers().contains(u)) {
               su.add(u);
            }
         }
         return su;
      } else if (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user)) {
         for (final User u : allUsers) {
            if (!object.getSecurityProfile().getReadUsers().contains(u)) {
               if (!u.isAdmin()) {
                  su.add(u);
               }
            }
         }
         return su;
      }
      return Collections.emptySet();
   }

   /**
    * Gets the list of users that have been allowed to write the given SecurableByProfile object. The supplied User acts as a basis for the
    * Users that are shown in the returned list. The following rules apply:
    * <p/>
    * If the supplied basis User is an admin, all users are shown.<br/>
    * If the basis User isn't an admin, but owns the object then all Users apart from admins are shown.<br/>
    * If the basis User isn't an admin, and doesn't own the object, then no Users will be returned.
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @return Set<User>
    */
   public static Set<User> getSelectedWriteUsers(User user, SecurableByProfile object) {
      final SortedSet<User> su = new TreeSet<User>(new FullNameComparator());
      if (user.isAdmin()) {
         return new TreeSet<User>(object.getSecurityProfile().getWriteUsers());
      } else if (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user)) {
         for (final User u : object.getSecurityProfile().getWriteUsers()) {
            if (!u.isAdmin()) {
               su.add(u);
            }
         }
         return su;
      }
      return Collections.emptySet();
   }

   /**
    * Gets the list of users that can be set to be allowed to write SecurableByProfile object. The supplied User acts as a basis for the
    * Users that are shown in the returned list. The following rules apply:
    * <p/>
    * If the supplied basis User is an admin, all users that can't already write the SecurableByProfile object are shown.<br/>
    * If the basis User isn't an admin, but owns the object, then all Users apart from admins that can't already write the
    * SecurableByProfile object are shown.<br/>
    * If the basis User isn't an admin, and doesn't own the object, then no Users will be returned.
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @param allUsers
    *           of type Collection<User>
    * @return Set<User>
    */
   public static Set<User> getAvailableWriteUsers(User user, SecurableByProfile object, Collection<User> allUsers) {
      final SortedSet<User> su = new TreeSet<User>(new FullNameComparator());
      if (user.isAdmin()) {
         for (final User u : allUsers) {
            if (!object.getSecurityProfile().getWriteUsers().contains(u)) {
               su.add(u);
            }
         }
         return su;
      } else if (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user)) {
         for (final User u : allUsers) {
            if (!object.getSecurityProfile().getWriteUsers().contains(u)) {
               if (!u.isAdmin()) {
                  su.add(u);
               }
            }
         }
         return su;
      }
      return Collections.emptySet();
   }

   /**
    * From a subset of supplied Groups (usually the list of all Groups in MISO), returns a collection of Groups that can be set to
    * read/write the supplied SecurableByProfile object This method will NOT return special groups, like "Watchers" groups, etc
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @param allGroups
    *           of type Collection<Group>
    * @return Set<Group>
    */
   public static Set<Group> getAccessibleGroups(User user, SecurableByProfile object, Collection<Group> allGroups) {
      final SortedSet<Group> su = new TreeSet<Group>();
      if (user.isAdmin() || (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user))) {
         for (final Group g : allGroups) {
            if (!g.getName().endsWith("Watchers")) {
               su.add(g);
            }
         }
         return su;
      }
      return Collections.emptySet();
   }

   /**
    * Gets the list of Groups that have been allowed to read the given SecurableByProfile object. The supplied User acts as a basis for the
    * Groups that are shown in the returned list. The following rules apply:
    * <p/>
    * If the supplied basis User is an admin, or is the owner of the SecurableByProfile object, all read Groups are shown.<br/>
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @return Set<User>
    */
   public static Set<Group> getSelectedReadGroups(User user, SecurableByProfile object) {
      final SortedSet<Group> su = new TreeSet<Group>();
      if (user.isAdmin() || (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user))) {
         return new TreeSet<Group>(object.getSecurityProfile().getReadGroups());
      }
      return Collections.emptySet();
   }

   /**
    * Gets the list of Groups that can be set to be allowed to read SecurableByProfile object. The supplied User acts as a basis for the
    * Groups that are shown in the returned list. The following rules apply:
    * <p/>
    * If the supplied basis User is an admin, or is the owner of the SecurableByProfile object, then all Groups that can't already read the
    * SecurableByProfile object are shown.<br/>
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @param allGroups
    *           of type Collection<Group>
    * @return Set<Group>
    */
   public static Set<Group> getAvailableReadGroups(User user, SecurableByProfile object, Collection<Group> allGroups) {
      final SortedSet<Group> su = new TreeSet<Group>();
      if (user.isAdmin() || (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user))) {
         for (final Group g : allGroups) {
            if (!object.getSecurityProfile().getReadGroups().contains(g)) {
               su.add(g);
            }
         }
         return su;
      }
      return Collections.emptySet();
   }

   /**
    * Gets the list of Groups that have been allowed to write the given SecurableByProfile object. The supplied User acts as a basis for the
    * Groups that are shown in the returned list. The following rules apply:
    * <p/>
    * If the supplied basis User is an admin, or is the owner of the SecurableByProfile object, all write Groups are shown.<br/>
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @return Set<User>
    */
   public static Set<Group> getSelectedWriteGroups(User user, SecurableByProfile object) {
      final SortedSet<Group> su = new TreeSet<Group>();
      if (user.isAdmin() || (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user))) {
         return new TreeSet<Group>(object.getSecurityProfile().getWriteGroups());
      }
      return Collections.emptySet();
   }

   /**
    * Gets the list of Groups that can be set to be allowed to write SecurableByProfile object. The supplied User acts as a basis for the
    * Groups that are shown in the returned list. The following rules apply:
    * <p/>
    * If the supplied basis User is an admin, or is the owner of the SecurableByProfile object, then all Groups that can't already write the
    * SecurableByProfile object are shown.<br/>
    *
    * @param user
    *           of type User
    * @param object
    *           of type SecurableByProfile
    * @param allGroups
    *           of type Collection<Group>
    * @return Set<Group>
    */
   public static Set<Group> getAvailableWriteGroups(User user, SecurableByProfile object, Collection<Group> allGroups) {
      final SortedSet<Group> su = new TreeSet<Group>();
      if (user.isAdmin() || (object.getSecurityProfile().getOwner() != null && object.getSecurityProfile().getOwner().equals(user))) {
         for (final Group g : allGroups) {
            if (!object.getSecurityProfile().getWriteGroups().contains(g)) {
               su.add(g);
            }
         }
         return su;
      }
      return Collections.emptySet();
   }

   private static class FullNameComparator implements Comparator<User> {
      @Override
      public int compare(User u1, User u2) {
         return u1.getFullName().compareTo(u2.getFullName());
      }
   }
}
