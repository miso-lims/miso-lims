/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.hibernate.SessionFactory;
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
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.impl.DefaultAlert;
import uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateAlertDao;

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
  private HibernateAlertDao dao;

  @Autowired
  private SessionFactory sessionFactory;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(template);
    dao.setSessionFactory(sessionFactory);
    User mockUser = Mockito.mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);
    when(securityManager.getUserById(anyLong())).thenReturn(mockUser);

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#remove(uk.ac.bbsrc.tgac.miso.core.event.Alert)}.
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
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#save(uk.ac.bbsrc.tgac.miso.core.event.Alert)}.
   * 
   * @throws IOException
   */
  @Test
  public void testSave() throws IOException {

    Date date = new Date();
    String title = "title";
    String text = "text";
    User user = new UserImpl();
    user.setUserId(1L);
    boolean isRead = true;
    AlertLevel level = AlertLevel.CRITICAL;

    Alert alert = new DefaultAlert(user);
    alert.setAlertTitle(title);
    alert.setAlertText(text);
    alert.setAlertDate(date);
    alert.setAlertRead(isRead);
    alert.setAlertLevel(level);

    long id = dao.save(alert);

    Alert returnedAlert = dao.get(id);

    assertEquals(title, returnedAlert.getAlertTitle());
    assertEquals(text, returnedAlert.getAlertText());
    assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(date),
        new SimpleDateFormat("yyyy-MM-dd").format(returnedAlert.getAlertDate()));
    assertEquals(isRead, returnedAlert.getAlertRead());
    assertEquals(level, returnedAlert.getAlertLevel());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#get(long)}.
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
    assertEquals("2012-04-20", new SimpleDateFormat("yyyy-MM-dd").format(alert.getAlertDate()));
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#lazyGet(long)}.
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
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#listAll()}.
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
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#count()}.
   * 
   * @throws IOException
   */
  @Test
  public void testCount() throws IOException {
    int count = dao.count();
    assertEquals(3, count);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#listByUserId(long)}.
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
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#listByUserId(long, long)}.
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
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#listByAlertLevel(uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel)}.
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
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#listUnreadByUserId(long)}.
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
   * {@link uk.ac.bbsrc.tgac.miso.sqlstore.HibernateAlertDao#listUnreadByAlertLevel(uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel)}.
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
