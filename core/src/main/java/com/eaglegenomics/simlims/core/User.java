package com.eaglegenomics.simlims.core;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * com.eaglegenomics.simlims.core
 * <p/>
 * A User interface to describe Users of a LIMS system
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface User extends Serializable, Comparable {
  boolean isActive();

  void setActive(boolean active);

  Long getUserId();

  void setUserId(Long userId);

  String getEmail();

  void setEmail(String email);

  String getFullName();

  String getPassword();

  Collection<Group> getGroups();

  String getLoginName();

  String[] getRoles();

  Collection<GrantedAuthority> getRolesAsAuthorities();

  Collection<GrantedAuthority> getPermissionsAsAuthorities();

  boolean isAdmin();

  boolean isExternal();

  boolean isInternal();

  void setAdmin(boolean admin);

  void setExternal(boolean external);

  void setFullName(String fullName);

  void setPassword(String password);

  void setGroups(Collection<Group> groups);

  void setInternal(boolean internal);

  void setLoginName(String loginName);

  void setRoles(String[] roles);
}
