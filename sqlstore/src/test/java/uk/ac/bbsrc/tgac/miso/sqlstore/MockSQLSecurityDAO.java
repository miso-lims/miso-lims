package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 08/10/12
 * @since 0.1.9
 */
public class MockSQLSecurityDAO extends SQLSecurityDAO {
  private final Map<Long, User> usermap = Collections.synchronizedMap(new HashMap<Long, User>());
  private final Map<Long, Collection<Group>> groupmap = Collections.synchronizedMap(new HashMap<Long, Collection<Group>>());

  @Override
  public User getUserById(Long userId) throws IOException {
    synchronized (usermap) {
      if (!usermap.containsKey(userId)) {
        User u = super.getUserById(userId);
        if (u != null) {
          usermap.put(userId, u);
        }
        return u;
      } else {
        return usermap.get(userId);
      }
    }
  }

  @Override
  public Collection<Group> listGroupsByUserId(Long userId) throws IOException {
    synchronized (groupmap) {
      if (!groupmap.containsKey(userId)) {
        Collection<Group> gs = super.listGroupsByUserId(userId);
        groupmap.put(userId, gs);
        return gs;
      } else {
        return groupmap.get(userId);
      }
    }
  }

  public void clearCaches() {
    usermap.clear();
    groupmap.clear();
  }
}
