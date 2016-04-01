/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.impl.DefaultAlert;
import uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel;

/**
 * @author Chris Salt
 *
 */
public class SQLAlertDAOTest extends AbstractDAOTest {

  @Autowired
  @Spy
  private JdbcTemplate template;

  @Mock
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @InjectMocks
  private SQLAlertDAO dao;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(template);
    User mockUser = Mockito.mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);
    when(securityManager.getUserById(anyLong())).thenReturn(mockUser);

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#remove(uk.ac.bbsrc.tgac.miso.core.event.Alert)}.
   * 
   * @throws IOException
   */
  @Test
  public void testRemove() throws IOException {
    assertEquals(3, dao.count());
    Alert alert = dao.get(1L);
    dao.remove(alert);
    Collection<Alert> alerts = dao.listAll();
    assertEquals(2, alerts.size());
    for (Alert a : alerts) {
      assertFalse(1L == a.getAlertId());
    }
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#save(uk.ac.bbsrc.tgac.miso.core.event.Alert)}.
   * 
   * @throws IOException
   */
  @Test
  public void testSave() throws IOException {

    Alert alert = new DefaultAlert();
    Date date = new Date();
    String title = "title";
    String text = "text";
    long userId = 1L;
    boolean isRead = true;
    AlertLevel level = AlertLevel.CRITICAL;

    alert.setAlertTitle(title);
    alert.setAlertText(text);
    alert.setAlertDate(date);
    alert.setAlertRead(isRead);
    alert.setAlertLevel(level);

    long id = dao.save(alert);

    Alert returnedAlert = dao.get(id);

    assertEquals(title, returnedAlert.getAlertTitle());
    assertEquals(text, returnedAlert.getAlertText());
    assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(date), returnedAlert.getAlertDate().toString());
    assertEquals(isRead, returnedAlert.getAlertRead());
    assertEquals(level, returnedAlert.getAlertLevel());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#get(long)}.
   * 
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public void testGet() throws IOException, ParseException {
    Alert alert = dao.get(1L);
    assertEquals(new Long(1), alert.getAlertId());
    assertEquals("Alert 1", alert.getAlertTitle());
    assertEquals("Alert 1 Text", alert.getAlertText());
    assertEquals(new Long(1), alert.getAlertUser().getUserId());
    assertEquals(false, alert.getAlertRead());
    assertEquals(AlertLevel.INFO, alert.getAlertLevel());
    assertEquals("2012-04-20", alert.getAlertDate().toString());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#lazyGet(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testLazyGet() throws IOException {
    Alert alert = dao.get(1L);
    assertEquals(new Long(1), alert.getAlertId());
    assertEquals("Alert 1", alert.getAlertTitle());
    assertEquals("Alert 1 Text", alert.getAlertText());
    assertEquals(new Long(1), alert.getAlertUser().getUserId());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#listAll()}.
   * 
   * @throws IOException
   */
  @Test
  public void testListAll() throws IOException {
    Collection<Alert> alerts = dao.listAll();
    assertNotNull(alerts);
    assertEquals(3, alerts.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#count()}.
   * 
   * @throws IOException
   */
  @Test
  public void testCount() throws IOException {
    int count = dao.count();
    assertEquals(3, count);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#listByUserId(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testListByUserIdLong() throws IOException {
    Collection<Alert> alerts = dao.listByUserId(1L);
    assertNotNull(alerts);
    assertEquals(3, alerts.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#listByUserId(long, long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testListByUserIdLongLong() throws IOException {
    Collection<Alert> alerts = dao.listByUserId(1L, 2L);
    assertNotNull(alerts);
    assertEquals(2, alerts.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#listByAlertLevel(uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel)}.
   * 
   * @throws IOException
   */
  @Test
  public void testListByAlertLevel() throws IOException {
    Collection<Alert> alerts = dao.listByAlertLevel(AlertLevel.INFO);
    assertNotNull(alerts);
    assertEquals(3, alerts.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#listUnreadByUserId(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testListUnreadByUserId() throws IOException {
    Collection<Alert> alerts = dao.listUnreadByUserId(1L);
    assertNotNull(alerts);
    assertEquals(2, alerts.size());

  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLAlertDAO#listUnreadByAlertLevel(uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel)}.
   * 
   * @throws IOException
   */
  @Test
  public void testListUnreadByAlertLevel() throws IOException {
    Collection<Alert> alerts = dao.listUnreadByAlertLevel(AlertLevel.INFO);
    assertNotNull(alerts);
    assertEquals(2, alerts.size());
  }

}
