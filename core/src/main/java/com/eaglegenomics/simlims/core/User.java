package com.eaglegenomics.simlims.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;

/**
 * com.eaglegenomics.simlims.core
 * <p/>
 * A User interface to describe Users of a LIMS system
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface User extends Serializable, Comparable<User>, Deletable {
  boolean isActive();

  void setActive(boolean active);

  String getEmail();

  void setEmail(String email);

  String getFullName();

  String getPassword();

  Set<Group> getGroups();

  String getLoginName();

  String[] getRoles();

  Collection<GrantedAuthority> getRolesAsAuthorities();

  Collection<GrantedAuthority> getPermissionsAsAuthorities();

  boolean isAdmin();

  boolean isInternal();

  boolean isRunReviewer();

  Set<WorkflowName> getFavouriteWorkflows();

  void setAdmin(boolean admin);

  void setFullName(String fullName);

  void setPassword(String password);

  void setGroups(Set<Group> groups);

  void setInternal(boolean internal);

  void setLoginName(String loginName);

  void setRoles(String[] roles);

  void setFavouriteWorkflows(Set<WorkflowName> favouriteWorkflows);

  @Override
  public int hashCode();

  @Override
  public boolean equals(Object obj);

}
