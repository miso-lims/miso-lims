package com.eaglegenomics.simlims.core.manager;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.store.SecurityStore;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Empty implementation.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
@Transactional(rollbackFor = Exception.class)
public abstract class AbstractSecurityManager implements SecurityManager {
  private Collection<String> defaultRoles;

  @Autowired
  protected SecurityStore securityStore;

  @Override
  public Collection<String> getDefaultRoles() {
    return this.defaultRoles;
  }

  @Override
  public Group getGroupById(Long groupId) throws IOException {
    return securityStore.getGroupById(groupId);
  }

  @Override
  public Group getGroupByName(String groupName) throws IOException {
    return securityStore.getGroupByName(groupName);
  }

  @Override
  public SecurityProfile getSecurityProfileById(Long profileId) throws IOException {
    return securityStore.getSecurityProfileById(profileId);
  }

  @Override
  public User getUserByEmail(String email) throws IOException {
    return securityStore.getUserByEmail(email);
  }

  @Override
  public User getUserById(Long userId) throws IOException {
    return securityStore.getUserById(userId);
  }

  @Override
  public User getUserByLoginName(String username) throws IOException {
    return securityStore.getUserByLoginName(username);
  }

  @Override
  public Collection<Group> listAllGroups() throws IOException {
    return securityStore.listAllGroups();
  }

  @Override
  public Collection<User> listAllUsers() throws IOException {
    return securityStore.listAllUsers();
  }

  @Override
  public Collection<Group> listGroupsByIds(Collection<Long> groupIds) throws IOException {
    return securityStore.listGroupsByIds(groupIds);
  }

  @Override
  public Collection<User> listUsersByGroupName(String name) throws IOException {
    return securityStore.listUsersByGroupName(name);
  }

  @Override
  public Collection<User> listUsersByIds(Collection<Long> userIds) throws IOException {
    return securityStore.listUsersByIds(userIds);
  }

  @Override
  public long saveGroup(Group group) throws IOException {
    return securityStore.saveGroup(group);
  }

  @Override
  public long saveUser(User user) throws IOException {
    long id;
    User original = securityStore.getUserByLoginName(user.getLoginName());
    if (original == null) {
      id = securityStore.saveUser(user);
    } else {
      original.setActive(user.isActive());
      original.setAdmin(user.isAdmin());
      original.setEmail(user.getEmail());
      original.setExternal(user.isExternal());
      original.setInternal(user.isInternal());
      original.setFullName(user.getFullName());
      original.setRoles(user.getRoles());
      if (isPasswordMutable()) {
        if (user.getPassword() != null) {
          original.setPassword(user.getPassword());
        }
      } else {
        original.setPassword(null);
      }
      id = securityStore.saveUser(original);
    }
    return id;
  }

  public void setDefaultRoles(Collection<String> defaultRoles) {
    this.defaultRoles = defaultRoles;
  }

  /**
   * The manager needs the store to persist security details. It has no
   * default store.
   */
  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }
}
