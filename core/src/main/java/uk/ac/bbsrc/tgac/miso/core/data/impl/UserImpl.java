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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.ac.bbsrc.tgac.miso.core.security.MisoAuthority;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Concrete implementation of a User object, inheriting from the simlims core User
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "User")
public class UserImpl implements User, Serializable {
  protected static final Logger log = LoggerFactory.getLogger(UserImpl.class);

  private static final long serialVersionUID = 1L;

  /**
   * Use this ID to indicate that a user has not yet been saved, and therefore does not yet have a unique ID.
   */
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long userId = UserImpl.UNSAVED_ID;
  private String fullName = "";
  private String loginName = "";
  private String email = "";
  private String password = "";
  private boolean internal = false;
  private boolean external = false;
  private boolean admin = false;
  private boolean active = true;

  @ManyToMany(targetEntity = Group.class)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(name = "User_Group", inverseJoinColumns = { @JoinColumn(name = "groups_groupId") }, joinColumns = {
      @JoinColumn(name = "users_userId") })
  private Collection<Group> groups = new HashSet<>();

  @Lob
  private String roles = new String();

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public Long getUserId() {
    return userId;
  }

  @Override
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public long getId() {
    return userId;
  }

  public void setId(long userId) {
    this.userId = userId;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getFullName() {
    return fullName;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<Group> getGroups() {
    return groups;
  }

  @Override
  public String getLoginName() {
    return loginName;
  }

  @Override
  public String[] getRoles() {
    return roles.split(",");
  }

  @Override
  @JsonIgnore
  public Collection<GrantedAuthority> getRolesAsAuthorities() {
    List<GrantedAuthority> auths = new ArrayList<>();
    for (String s : getRoles()) {
      auths.add(new SimpleGrantedAuthority(s));
    }
    return auths;
  }

  @Override
  public Collection<GrantedAuthority> getPermissionsAsAuthorities() {
    List<GrantedAuthority> auths = new ArrayList<>();
    if (isAdmin()) {
      auths.add(MisoAuthority.ROLE_ADMIN);
    }

    if (isInternal()) {
      auths.add(MisoAuthority.ROLE_INTERNAL);
    }

    if (isExternal()) {
      auths.add(MisoAuthority.ROLE_EXTERNAL);
    }

    return auths;
  }

  @Override
  public boolean isAdmin() {
    return admin;
  }

  @Override
  public boolean isExternal() {
    return external;
  }

  @Override
  public boolean isInternal() {
    return internal;
  }

  @Override
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  @Override
  public void setExternal(boolean external) {
    this.external = external;
  }

  @Override
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public void setGroups(Collection<Group> groups) {
    this.groups = groups;
  }

  @Override
  public void setInternal(boolean internal) {
    this.internal = internal;
  }

  @Override
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  @Override
  public void setRoles(String[] roles) {
    StringBuilder roleString = new StringBuilder();
    for (String role : roles) {
      if (!LimsUtils.isStringEmptyOrNull(role)) roleString.append(role);
    }
    this.roles = roleString.toString();
  }

  /**
   * Users are equated by login name.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof User)) return false;
    User them = (User) obj;
    if (getId() == UserImpl.UNSAVED_ID || them.getUserId() == UserImpl.UNSAVED_ID) {
      return this.getLoginName().equals(them.getLoginName());
    } else {
      return this.getId() == them.getUserId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != UserImpl.UNSAVED_ID) {
      return ((Long) getId()).intValue();
    } else {
      int hashcode = 1;
      if (getLoginName() != null) hashcode = 37 * hashcode + getLoginName().hashCode();
      if (getEmail() != null) hashcode = 37 * hashcode + getEmail().hashCode();
      return hashcode;
    }
  }

  /**
   * Equivalent to getLoginName().
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getLoginName());
    sb.append(":").append(getFullName());
    sb.append(":").append(getEmail());
    sb.append(":").append(isActive());
    sb.append(":").append(isAdmin());
    sb.append(":").append(isInternal());
    sb.append(":").append(isExternal());
    sb.append("[").append(LimsUtils.join(getRoles(), ",")).append("]");
    return sb.toString();
  }

  @Override
  public int compareTo(User t) {
    if (getId() < t.getUserId()) return -1;
    if (getId() > t.getUserId()) return 1;
    return 0;
  }
}