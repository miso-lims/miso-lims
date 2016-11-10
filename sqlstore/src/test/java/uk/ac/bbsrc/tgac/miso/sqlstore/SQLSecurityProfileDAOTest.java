package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class SQLSecurityProfileDAOTest extends AbstractDAOTest {

  private static final User USER = new UserImpl();
  private static final Group GROUP = new Group();
  static {
    USER.setUserId(1L);
    GROUP.setGroupId(2L);
    GROUP.setName("THEGROUP");
  }

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  private final SecurityManager securityManager = new SecurityManager() {

    @Override
    public long saveUser(User arg0) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public long saveGroup(Group arg0) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Collection<User> listUsersByIds(Collection<Long> ids) throws IOException {
      List<User> users = new ArrayList<>();
      for (long id : ids) {
        users.add(getUserById(id));
      }

      return users;
    }

    @Override
    public Collection<User> listUsersByGroupName(String arg0) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Group> listGroupsByIds(Collection<Long> ids) throws IOException {
      List<Group> groups = new ArrayList<>();
      for (long id : ids) {
        if (id == 2) {
          groups.add(GROUP);
        } else {
          throw new UnsupportedOperationException();
        }
      }
      return groups;
    }

    @Override
    public Collection<User> listAllUsers() throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Group> listAllGroups() throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public User getUserByLoginName(String arg0) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public User getUserById(Long id) throws IOException {
      if (id == null) {
        return null;
      }
      User u = new UserImpl();
      u.setUserId(id);
      return u;
    }

    @Override
    public User getUserByEmail(String arg0) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public SecurityProfile getSecurityProfileById(Long arg0) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Group getGroupByName(String arg0) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Group getGroupById(Long arg0) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getDefaultRoles() {
      throw new UnsupportedOperationException();
    }
  };

  @InjectMocks
  private SQLSecurityProfileDAO dao;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSecurityManager(securityManager);
  }

  @Test
  public void testCount() throws IOException {
    assertTrue("No security profiles in DB", dao.count() > 0);
  }

  @Test
  public void testGetByIdNone() throws IOException {
    assertNull(dao.get(100L).getProfileId());
  }

  private void testById(long id, int rg, int wg, int ru, int wu) throws IOException {
    SecurityProfile result = dao.get(id);
    assertNotNull(result);
    assertTrue(result.getProfileId() == id);
    assertTrue(result.isAllowAllInternal());
    assertEquals(result.getOwner(), USER);
    assertEquals(result.getReadUsers().size(), ru);
    assertEquals(result.getWriteUsers().size(), wu);
    assertEquals(result.getReadGroups().size(), rg);
    assertEquals(result.getWriteGroups().size(), wg);
  }

  @Test
  public void testGetById1() throws IOException {
    testById(1L, 0, 0, 1, 0);
  }

  @Test
  public void testGetById2() throws IOException {
    testById(2L, 0, 0, 0, 1);
  }

  @Test
  public void testGetById3() throws IOException {
    testById(3L, 1, 0, 0, 0);
  }

  @Test
  public void testGetById4() throws IOException {
    testById(4L, 0, 1, 0, 0);
  }

  @Test
  public void testListAll() throws IOException {
    assertTrue(dao.listAll().size() > 0);
  }

  @Test
  public void testSaveNew() throws IOException {
    SecurityProfile profile = new SecurityProfile();
    profile.setOwner(USER);
    profile.setAllowAllInternal(true);
    profile.setReadGroups(Collections.singletonList(GROUP));
    long id = dao.save(profile);
    assertTrue(id > 0);
    assertTrue(dao.get(id).getReadGroups().size() > 0);
  }

  @Test
  public void testSaveEdit() throws IOException {
    SecurityProfile profile = dao.get(1L);
    assertTrue(profile.getWriteUsers().size() == 0);
    profile.getWriteUsers().add(USER);
    dao.save(profile);
    SecurityProfile rereadProfile = dao.get(1L);
    assertTrue(rereadProfile.getWriteUsers().size() == 1);
  }
}
