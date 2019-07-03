package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

public interface SecurityStore {

  public long saveUser(User user) throws IOException;

  public User getUserById(Long userId) throws IOException;

  public User getUserByLoginName(String loginName) throws IOException;

  public long saveGroup(Group group) throws IOException;

  public Group getGroupById(Long groupId) throws IOException;

  public Group getGroupByName(String loginName) throws IOException;

  public List<User> listAllUsers() throws IOException;

  public List<Group> listAllGroups() throws IOException;
  
}
