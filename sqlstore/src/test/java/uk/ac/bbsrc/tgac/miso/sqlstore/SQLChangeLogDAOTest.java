package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateChangeLogDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSecurityDao;

public class SQLChangeLogDAOTest extends AbstractDAOTest {

  @Mock
  private HibernateSecurityDao securityDAO;
  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateChangeLogDao sut;

  @InjectMocks
  private HibernateLibraryDao libraryDao;

  User user = new UserImpl();

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    user.setUserId(1L);
    when(securityDAO.getUserById(anyLong())).thenReturn(user);
    sut.setSessionFactory(sessionFactory);
    libraryDao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListAll() throws Exception {
    Collection<ChangeLog> list = sut.listAll("sample");
    assertNotNull(list);
    assertEquals(19, list.size());
  }

  @Test
  public void testListAllById() throws Exception {
    Collection<ChangeLog> list = sut.listAllById("sample", 1L);
    assertNotNull(list);
    assertEquals(3, list.size());
  }

  @Test
  public void testDeleteAllById() throws Exception {
    assertEquals(3, sut.listAllById("sample", 1L).size());
    sut.deleteAllById("sample", 1L);
    Collection<ChangeLog> list = sut.listAllById("sample", 1L);
    assertNotNull(list);
    assertEquals(0, list.size());
  }

  @Test
  public void testCreate() throws Exception {
    Collection<ChangeLog> list = sut.listAllById("library", 1L);
    assertNotNull(list);
    assertEquals(1, list.size());

    Library library = libraryDao.get(1L);
    ChangeLog changeLog = library.createChangeLog("Things have changed.", "Columns that have changed.", user);

    sut.create(changeLog);
    Collection<ChangeLog> newList = sut.listAllById("library", 1L);
    assertNotNull(newList);
    assertEquals(2, newList.size());
  }

  @Test
  public void testMapping() throws Exception {
    // (7, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:01')
    Collection<ChangeLog> list = sut.listAllById("sample", 7L);
    assertNotNull(list);
    assertEquals(1, list.size());
    ChangeLog cl = list.iterator().next();
    assertEquals("qcPassed", cl.getColumnsChanged());
    assertEquals(Long.valueOf(1L), cl.getUser().getUserId());
    assertEquals("false -> true", cl.getSummary());
    Date date = new Date(ISODateTimeFormat.dateTimeParser().parseDateTime("2016-07-07T13:31:01").getMillis());
    assertEquals(date, cl.getTime());
  }

}
