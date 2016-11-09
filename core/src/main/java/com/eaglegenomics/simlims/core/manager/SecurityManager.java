package com.eaglegenomics.simlims.core.manager;

import java.io.IOException;
import java.util.Collection;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p/>
 * The manager handles security features such as locating and logging in users.
 * It is backed by a SecurityStore, although that is implementation-specific and
 * is not part of the interface.
 * <p/>
 * All methods throw IOException because they may have recourse to backing
 * stores on disk or databases.
 *
 * @author Richard Holland
 * @since 0.0.1
 */
public interface SecurityManager {

  public Collection<String> getDefaultRoles();

  public User getUserByLoginName(String username) throws IOException;

  public Collection<User> listAllUsers() throws IOException;

  public Collection<User> listUsersByIds(Collection<Long> userIds) throws IOException;

  public Collection<User> listUsersByGroupName(String name) throws IOException;

  public long saveUser(User user) throws IOException;

  public Collection<Group> listAllGroups() throws IOException;

  public Collection<Group> listGroupsByIds(Collection<Long> groupIds) throws IOException;

  public long saveGroup(Group group) throws IOException;

  public User getUserById(Long userId) throws IOException;

  public User getUserByEmail(String email) throws IOException;

  public Group getGroupById(Long userId) throws IOException;

  public Group getGroupByName(String groupName) throws IOException;

  public SecurityProfile getSecurityProfileById(Long profileId) throws IOException;
}
