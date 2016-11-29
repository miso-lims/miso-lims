package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Watchable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class SQLWatcherDAOTest extends AbstractDAOTest {
  private static final User USER = new UserImpl();
  static {
    USER.setUserId(1L);
  }

  private static class DummyWatchable implements Watchable {

    private final String value;

    public DummyWatchable(String value) {
      super();
      this.value = value;
    }

    @Override
    public void setWatchers(Set<User> watchers) {
      throw new NotImplementedException();
    }

    @Override
    public void removeWatcher(User user) {
      throw new NotImplementedException();
    }

    @Override
    public Set<User> getWatchers() {
      throw new NotImplementedException();
    }

    @Override
    public String getWatchableIdentifier() {
      return value;
    }

    @Override
    public void addWatcher(User user) {
      throw new NotImplementedException();
    }
  }

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Mock
  private SecurityManager securityManager;

  @InjectMocks
  private SQLWatcherDAO dao;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSecurityManager(securityManager);
    Mockito.when(securityManager.getUserById(1L)).thenReturn(USER);
    Mockito.when(securityManager.listUsersByGroupName("g")).thenReturn(Collections.singletonList(USER));
  }

  @Test
  public void testGetWatchersByWatcherGroup() throws IOException {
    Collection<User> users = dao.getWatchersByWatcherGroup("g");
    Assert.assertEquals(1, users.size());
    Assert.assertEquals(USER, users.iterator().next());
  }

  @Test
  public void testRemoveWatchedEntity() throws IOException {
    Assert.assertEquals(1, dao.getWatchersByEntityName("IPO1").size());
    dao.removeWatchedEntity(new DummyWatchable("IPO1"));
    Assert.assertEquals(0, dao.getWatchersByEntityName("IPO1").size());
  }

  @Test
  public void testRemoveWatchedEntityByUser() throws IOException {
    int originalSize = dao.getWatchersByEntityName("IPO1").size();
    Assert.assertTrue(dao.removeWatchedEntityByUser(new DummyWatchable("IPO1"), USER));
    Assert.assertEquals(originalSize - 1, dao.getWatchersByEntityName("IPO1").size());
  }

  @Test
  public void testRemoveWatchedEntityByUserNonExistent() throws IOException {
    int originalSize = dao.getWatchersByEntityName("NotAThing").size();
    Assert.assertFalse(dao.removeWatchedEntityByUser(new DummyWatchable("NotAThing"), USER));
    Assert.assertEquals(originalSize, dao.getWatchersByEntityName("NotAThing").size());
  }

  @Test
  public void testSaveWatchedEntityUser() throws IOException {
    int originalSize = dao.getWatchersByEntityName("TEST").size();
    dao.saveWatchedEntityUser(new DummyWatchable("TEST"), USER);
    Assert.assertEquals(originalSize + 1, dao.getWatchersByEntityName("TEST").size());
  }

}
