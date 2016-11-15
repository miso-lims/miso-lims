package com.eaglegenomics.simlims.core.manager;

import java.io.IOException;
import java.util.Collection;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.store.SecurityStore;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p/>
 * Basic implementation using local stores. More complex implementations may
 * choose to use web services to communicate with a remote store, or to combine
 * multiple stores.
 *
 * @author Richard Holland
 * @since 0.0.1
 */
public class LocalSecurityManager extends AbstractSecurityManager {

  private SecurityStore securityStore;

  private Collection<String> defaultRoles;

  public Collection<String> getDefaultRoles() {
    return this.defaultRoles;
  }

  public void setDefaultRoles(Collection<String> defaultRoles) {
    this.defaultRoles = defaultRoles;
  }

  public User getUserByLoginName(String username) throws IOException {
    return securityStore.getUserByLoginName(username);
  }

  public Collection<User> listAllUsers() throws IOException {
    return securityStore.listAllUsers();
  }

  public Collection<User> listUsersByIds(Collection<Long> userIds) throws IOException {
    return securityStore.listUsersByIds(userIds);
  }

  public Collection<User> listUsersByGroupName(String name) throws IOException {
    return securityStore.listUsersByGroupName(name);
  }

  public long saveUser(User user) throws IOException {
    return securityStore.saveUser(user);
  }

  public Collection<Group> listAllGroups() throws IOException {
    return securityStore.listAllGroups();
  }

  public Collection<Group> listGroupsByIds(Collection<Long> groupIds) throws IOException {
    return securityStore.listGroupsByIds(groupIds);
  }

  public long saveGroup(Group group) throws IOException {
    return securityStore.saveGroup(group);
  }

  public SecurityProfile getSecurityProfileById(Long profileId) throws IOException {
    return securityStore.getSecurityProfileById(profileId);
  }

  /**
   * The manager needs the store to persist security details. It has no
   * default store.
   */
  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public User getUserById(Long userId) throws IOException {
    return securityStore.getUserById(userId);
  }

  public User getUserByEmail(String email) throws IOException {
    return securityStore.getUserByEmail(email);
  }

  public Group getGroupById(Long groupId) throws IOException {
    return securityStore.getGroupById(groupId);
  }

  public Group getGroupByName(String groupName) throws IOException {
    return securityStore.getGroupByName(groupName);
  }
}
