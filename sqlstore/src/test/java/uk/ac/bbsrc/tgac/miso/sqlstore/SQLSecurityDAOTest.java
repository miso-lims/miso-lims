package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSecurityDao;

public class SQLSecurityDAOTest extends AbstractDAOTest {
  
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;
  
  @Mock
  private Store<SecurityProfile> securityProfileDAO;
  
  @InjectMocks
  private HibernateSecurityDao dao;
  
  private static long nextUserId = 4L;
  private static long nextGroupId = 4L;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
  }
  
  private void mockAutoIncrement(long value) {
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
  }
  
  @Test
  public void testGetUserById() throws IOException {
    User user = dao.getUserById(1L);
    assertNotNull(user);
    assertEquals(Long.valueOf(1L), user.getUserId());
    assertEquals(true, user.isActive());
    assertEquals(false, user.isExternal());
    assertEquals("admin", user.getFullName());
    assertEquals(true, user.isInternal());
    assertEquals("admin", user.getLoginName());
    assertEquals("admin@admin", user.getEmail());
  }
  
  @Test
  public void testGetUserByIdNone() throws IOException {
    assertNull(dao.getUserById(100L));
  }
  
  @Test
  public void testGetUserByIdNull() throws IOException {
    assertNull(dao.getUserById(null));
  }
  
  @Test
  public void testUserByLoginName() throws IOException {
    User user = dao.getUserByLoginName("admin");
    assertNotNull(user);
    assertEquals("admin", user.getLoginName());
  }
  
  @Test
  public void testGetUserByLoginNameNone() throws IOException {
    assertNull(dao.getUserByLoginName("nonexistant user"));
  }
  
  @Test
  public void testGetUserByLoginNameNull() throws IOException {
    assertNull(dao.getUserByLoginName(null));
  }
  
  @Test
  public void testUserByEmail() throws IOException {
    User user = dao.getUserByEmail("admin@admin");
    assertNotNull(user);
    assertEquals("admin@admin", user.getEmail());
  }
  
  @Test
  public void testGetUserByEmailNone() throws IOException {
    assertNull(dao.getUserByEmail("nonexistant@email"));
  }
  
  @Test
  public void testGetUserByEmailNull() throws IOException {
    assertNull(dao.getUserByEmail(null));
  }
  
  @Test
  public void testListAllUsers() throws IOException {
    assertEquals(3, dao.listAllUsers().size());
  }
  
  @Test
  public void testListUsersByIds() throws IOException {
    List<Long> ids = new ArrayList<>();
    assertEquals(0, dao.listUsersByIds(ids).size());
    ids.add(1L);
    assertEquals(1, dao.listUsersByIds(ids).size());
    ids.add(3L);
    assertEquals(2, dao.listUsersByIds(ids).size());
    ids.add(100L);
    assertEquals(2, dao.listUsersByIds(ids).size());
  }
  
  @Test
  public void testListUsersByGroup() throws IOException {
    assertEquals(2, dao.listUsersByGroupName("RunWatchers").size());
  }
  
  @Test
  public void testListUsersByGroupNameNone() throws IOException {
    assertEquals(0, dao.listUsersByGroupName("nonexistant group").size());
  }
  
  @Test
  public void testListUsersByGroupNameNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.listUsersByGroupName(null).size();
  }
  
  @Test
  public void testSaveUserNew() throws IOException {
    User user = new UserImpl();
    user.setAdmin(false);
    user.setInternal(true);
    user.setExternal(true);
    user.setActive(true);
    user.setEmail("new@user.test");
    user.setFullName("Test User");
    user.setLoginName("testuser");
    user.setPassword("password");
    user.setRoles(new String[]{"ROLE_INTERNAL"});
    Collection<Group> groups = new HashSet<>();
    groups.add(dao.getGroupById(1L));
    user.setGroups(null);
    
    long autoIncrementId = nextUserId;
    assertNull(dao.getUserById(autoIncrementId));
    mockAutoIncrement(autoIncrementId);
    assertEquals(autoIncrementId, dao.saveUser(user));
    assertNotNull(dao.getUserById(autoIncrementId));
    nextUserId++;
  }
  
  @Test
  public void testSaveUserEdit() throws IOException {
    User user = dao.getUserById(3L);
    assertNotNull(user);
    user.setFullName("Test Edit");
    user.setExternal(false);
    user.setEmail("edited@user.test");
    assertEquals(3L, dao.saveUser(user));
    User saved = dao.getUserById(3L);
    assertEquals(user.getFullName(), saved.getFullName());
    assertEquals(user.isExternal(), saved.isExternal());
    assertEquals(user.getEmail(), saved.getEmail());
  }
  
  @Test
  public void testGetGroupById() throws IOException {
    Group group = dao.getGroupById(1L);
    assertNotNull(group);
    assertEquals(Long.valueOf(1L), group.getGroupId());
    assertEquals("RunWatchers", group.getName());
    assertEquals("Watches for all events on all runs", group.getDescription());
  }
  
  @Test
  public void testGetGroupByIdNone() throws IOException {
    assertNull(dao.getGroupById(100L));
  }
  
  @Test
  public void testGetGroupByIdNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.getGroupById(null);
  }
  
  @Test
  public void testGetGroupByName() throws IOException {
    Group group = dao.getGroupByName("RunWatchers");
    assertNotNull(group);
    assertEquals(Long.valueOf(1L), group.getGroupId());
  }
  
  @Test
  public void testGetGroupByNameNone() throws IOException {
    assertNull(dao.getGroupByName("nonexistant group"));
  }
  
  @Test
  public void testGetGroupByNameNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.getGroupByName(null);
  }
  
  @Test
  public void testListAllGroups() throws IOException {
    assertEquals(3, dao.listAllGroups().size());
  }
  
  @Test
  public void testListGroupsByUserId() throws IOException {
    assertEquals(1, dao.listGroupsByUserId(1L).size());
    assertEquals(0, dao.listGroupsByUserId(2L).size());
    assertEquals(2, dao.listGroupsByUserId(3L).size());
  }
  
  @Test
  public void testListGroupsByIds() throws IOException {
    List<Long> ids = new ArrayList<>();
    assertEquals(0, dao.listGroupsByIds(ids).size());
    ids.add(100L);
    assertEquals(0, dao.listGroupsByIds(ids).size());
    ids.add(1L);
    assertEquals(1, dao.listGroupsByIds(ids).size());
    ids.add(2L);
    assertEquals(2, dao.listGroupsByIds(ids).size());
  }
  
  @Test
  public void testSaveGroupNew() throws IOException {
    Group group = new Group();
    group.setName("testgroup");
    group.setDescription("test group");
    Collection<User> users = new HashSet<>();
    users.add(dao.getUserById(3L));
    group.setUsers(users);
    
    long autoIncrementId = nextGroupId;
    assertNull(dao.getGroupById(autoIncrementId));
    mockAutoIncrement(autoIncrementId);
    assertEquals(autoIncrementId, dao.saveGroup(group));
    assertNotNull(dao.getGroupById(autoIncrementId));
    nextGroupId++;
  }
  
  @Test
  public void testSaveGroupEdit() throws IOException {
    Group group = dao.getGroupById(3L);
    assertNotNull(group);
    group.setDescription("new description");
    assertEquals(group.getGroupId().longValue(), dao.saveGroup(group));
    Group saved = dao.getGroupById(3L);
    assertEquals(group.getDescription(), saved.getDescription());
  }
  
  @Test
  public void testGetUserColumnSizes() throws IOException {
    assertTrue(dao.getUserColumnSizes().size() > 9);
  }

  @Test
  public void testGetGroupColumnSizes() throws IOException {
    assertTrue(dao.getGroupColumnSizes().size() > 2);
  }
  
}
