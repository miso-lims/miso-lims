package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SampleChangeLog;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class HibernateChangeLogDaoIT extends AbstractDAOTest {

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
    user.setId(1L);
    when(securityDAO.getUserById(anyLong())).thenReturn(user);
    sut.setSessionFactory(sessionFactory);
    libraryDao.setSessionFactory(sessionFactory);
  }

  private Collection<ChangeLog> listSampleChangelogs(long libraryId) {
    @SuppressWarnings("unchecked")
    Collection<ChangeLog> list = currentSession().createCriteria(SampleChangeLog.class)
        .createAlias("sample", "sample")
        .add(Restrictions.eq("sample.id", libraryId))
        .list();
    return list;
  }

  @Test
  public void testCreate() throws Exception {
    long libraryId = 1L;
    Collection<ChangeLog> list = listLibraryChangelogs(libraryId);
    assertNotNull(list);
    assertEquals(1, list.size());

    Library library = libraryDao.get(libraryId);
    ChangeLog changeLog = library.createChangeLog("Things have changed.", "Columns that have changed.", user);
    changeLog.setTime(new Date());

    sut.create(changeLog);
    Collection<ChangeLog> newList = listLibraryChangelogs(libraryId);
    assertNotNull(newList);
    assertEquals(2, newList.size());
  }

  private Collection<ChangeLog> listLibraryChangelogs(long libraryId) {
    @SuppressWarnings("unchecked")
    Collection<ChangeLog> list = currentSession().createCriteria(LibraryChangeLog.class)
        .createAlias("library", "library")
        .add(Restrictions.eq("library.id", libraryId))
        .list();
    return list;
  }

  @Test
  public void testMapping() throws Exception {
    // (7, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:01')
    Collection<ChangeLog> list = listSampleChangelogs(7L);
    assertNotNull(list);
    assertEquals(1, list.size());
    ChangeLog cl = list.iterator().next();
    assertEquals("qcPassed", cl.getColumnsChanged());
    assertEquals(1L, cl.getUser().getId());
    assertEquals("false -> true", cl.getSummary());
    Date date = new Date(ISODateTimeFormat.dateTimeParser().parseDateTime("2016-07-07T13:31:01").getMillis());
    assertEquals(LimsUtils.formatDate(date), LimsUtils.formatDate(cl.getTime()));
  }

}
