package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;

import org.hibernate.SessionFactory;
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

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSecurityProfileDao;

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

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateSecurityProfileDao dao;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
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
