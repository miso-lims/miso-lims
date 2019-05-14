package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateSecurityDaoTest extends AbstractDAOTest {
  
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private SessionFactory sessionFactory;
  
  @InjectMocks
  private HibernateSecurityDao dao;
  
  private static long nextUserId = 4L;
  private static long nextGroupId = 4L;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
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
    assertEquals(1L, user.getId());
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
    exception.expect(Exception.class);
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
  public void testListAllUsers() throws IOException {
    assertEquals(3, dao.listAllUsers().size());
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
    assertEquals(1L, group.getId());
    assertEquals("TestGroup", group.getName());
    assertEquals("Is full of testing", group.getDescription());
  }
  
  @Test
  public void testGetGroupByIdNone() throws IOException {
    assertNull(dao.getGroupById(100L));
  }
  
  @Test
  public void testGetGroupByIdNull() throws IOException {
    exception.expect(Exception.class);
    dao.getGroupById(null);
  }
  
  @Test
  public void testGetGroupByName() throws IOException {
    Group group = dao.getGroupByName("TestGroup");
    assertNotNull(group);
    assertEquals(1L, group.getId());
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
    assertEquals(1, dao.listAllGroups().size());
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
    Group group = dao.getGroupById(1L);
    assertNotNull(group);
    group.setDescription("new description");
    assertEquals(group.getId(), dao.saveGroup(group));
    Group saved = dao.getGroupById(1L);
    assertEquals(group.getDescription(), saved.getDescription());
  }
  
}
