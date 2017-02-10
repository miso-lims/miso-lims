package com.eaglegenomics.simlims.core;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * com.eaglegenomics.simlims.core
 * <p/>
 * A User interface to describe Users of a LIMS system
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "roles", "password", "loginName", "groups", "admin", "internal", "external" })
public interface User extends Serializable, Comparable<User> {
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

  @JsonIgnore
  Collection<GrantedAuthority> getRolesAsAuthorities();

  @JsonIgnore
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
