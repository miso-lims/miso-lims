package com.eaglegenomics.simlims.core.store;

import java.io.IOException;
import java.util.Collection;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p/>
 * A repository for security information such as user accounts and groups. Use
 * the SecurityManager to perform all queries - never use a SecurityStore
 * directly.
 * <p/>
 * Note that the store provides no methods for directly deleting data. This
 * helps prevent accidental data loss and assists validation.
 * <p/>
 * Everything in this interface throws IOExceptions, as they all involve some
 * form of IO (whether it be to disk or database).
 *
 * @author Richard Holland
 * @since 0.0.1
 */
public interface SecurityStore {

  public long saveUser(User user) throws IOException;

  public User getUserById(Long userId) throws IOException;

  public User getUserByLoginName(String loginName) throws IOException;

  public User getUserByEmail(String email) throws IOException;

  public long saveGroup(Group group) throws IOException;

  public Group getGroupById(Long groupId) throws IOException;

  public Group getGroupByName(String loginName) throws IOException;

  public Collection<User> listAllUsers() throws IOException;

  public Collection<User> listUsersByIds(Collection<Long> userIds) throws IOException;

  public Collection<User> listUsersByGroupName(String name) throws IOException;

  public Collection<Group> listAllGroups() throws IOException;

  public Collection<Group> listGroupsByIds(Collection<Long> groupIds) throws IOException;

  public SecurityProfile getSecurityProfileById(Long profileId) throws IOException;
}
