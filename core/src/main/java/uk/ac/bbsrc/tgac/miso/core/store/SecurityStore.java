package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

public interface SecurityStore {

  public long saveUser(User user) throws IOException;

  public User getUserById(Long userId) throws IOException;

  public User getUserByLoginName(String loginName) throws IOException;

  public User getUserByEmail(String email) throws IOException;

  public User getUserByFullName(String fullName) throws IOException;

  public long saveGroup(Group group) throws IOException;

  public Group getGroupById(Long groupId) throws IOException;

  public Group getGroupByName(String loginName) throws IOException;

  public Collection<User> listAllUsers() throws IOException;

  public Collection<User> listUsersByIds(Collection<Long> userIds) throws IOException;

  public Collection<User> listUsersByGroupName(String name) throws IOException;

  public Collection<Group> listAllGroups() throws IOException;

  public Collection<Group> listGroupsByIds(Collection<Long> groupIds) throws IOException;

  public SecurityProfile getSecurityProfileById(Long profileId) throws IOException;

  /**
   * @return a map containing all column names and max lengths from the User table
   * @throws IOException
   */
  public Map<String, Integer> getUserColumnSizes() throws IOException;
  
  /**
   * @return a map containing all column names and max lengths from the Group table
   * @throws IOException
   */
  public Map<String, Integer> getGroupColumnSizes() throws IOException;
  
}
