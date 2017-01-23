package com.eaglegenomics.simlims.core.manager;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.ProjectAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;

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

  @Autowired
  private SecurityStore securityStore;
  @Autowired
  private PoolAlertManager poolAlertManager;
  @Autowired
  private RunAlertManager runAlertManager;
  @Autowired
  private ProjectAlertManager projectAlertManager;

  private Collection<String> defaultRoles;

  @Override
  public Collection<String> getDefaultRoles() {
    return this.defaultRoles;
  }

  public void setDefaultRoles(Collection<String> defaultRoles) {
    this.defaultRoles = defaultRoles;
  }

  @Override
  public User getUserByLoginName(String username) throws IOException {
    return securityStore.getUserByLoginName(username);
  }

  @Override
  public Collection<User> listAllUsers() throws IOException {
    return securityStore.listAllUsers();
  }

  @Override
  public Collection<User> listUsersByIds(Collection<Long> userIds) throws IOException {
    return securityStore.listUsersByIds(userIds);
  }

  @Override
  public Collection<User> listUsersByGroupName(String name) throws IOException {
    return securityStore.listUsersByGroupName(name);
  }

  @Override
  public long saveUser(User user) throws IOException {
    long id = securityStore.saveUser(user);
    runAlertManager.updateGroupWatcher(user);
    projectAlertManager.updateGroupWatcher(user);
    poolAlertManager.updateGroupWatcher(user);
    return id;
  }

  @Override
  public Collection<Group> listAllGroups() throws IOException {
    return securityStore.listAllGroups();
  }

  @Override
  public Collection<Group> listGroupsByIds(Collection<Long> groupIds) throws IOException {
    return securityStore.listGroupsByIds(groupIds);
  }

  @Override
  public long saveGroup(Group group) throws IOException {
    return securityStore.saveGroup(group);
  }

  @Override
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

  @Override
  public User getUserById(Long userId) throws IOException {
    return securityStore.getUserById(userId);
  }

  @Override
  public User getUserByEmail(String email) throws IOException {
    return securityStore.getUserByEmail(email);
  }

  @Override
  public Group getGroupById(Long groupId) throws IOException {
    return securityStore.getGroupById(groupId);
  }

  @Override
  public Group getGroupByName(String groupName) throws IOException {
    return securityStore.getGroupByName(groupName);
  }
}
