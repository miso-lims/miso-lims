package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

public interface SecurityStore {

  long saveUser(User user) throws IOException;

  User getUserById(Long userId) throws IOException;

  User getUserByLoginName(String loginName) throws IOException;

  List<User> listUsersBySearch(String search) throws IOException;

  long saveGroup(Group group) throws IOException;

  Group getGroupById(Long groupId) throws IOException;

  Group getGroupByName(String loginName) throws IOException;

  List<User> listAllUsers() throws IOException;

  List<Group> listAllGroups() throws IOException;

  long getUsageByTransfers(Group group) throws IOException;

}
