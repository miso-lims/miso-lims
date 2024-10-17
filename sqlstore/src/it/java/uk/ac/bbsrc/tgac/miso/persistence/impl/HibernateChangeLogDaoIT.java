package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SampleChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SampleChangeLog_;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class HibernateChangeLogDaoIT extends AbstractDAOTest {

  @Mock
  private HibernateSecurityDao securityDAO;
  @PersistenceContext
  private EntityManager entityManager;

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
    sut.setEntityManager(entityManager);
    libraryDao.setEntityManager(entityManager);
  }

  private Collection<ChangeLog> listSampleChangelogs(long libraryId) {
    QueryBuilder<ChangeLog, SampleChangeLog> builder =
        new QueryBuilder<>(currentSession(), SampleChangeLog.class, ChangeLog.class);
    Join<SampleChangeLog, SampleImpl> join = builder.getJoin(builder.getRoot(), SampleChangeLog_.sample);
    builder.addPredicate(builder.getCriteriaBuilder().equal(join.get(SampleImpl_.sampleId), libraryId));
    return builder.getResultList();
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
    QueryBuilder<ChangeLog, LibraryChangeLog> builder =
        new QueryBuilder<>(currentSession(), LibraryChangeLog.class, ChangeLog.class);
    Join<LibraryChangeLog, LibraryImpl> join = builder.getJoin(builder.getRoot(), LibraryChangeLog_.library);
    builder.addPredicate(builder.getCriteriaBuilder().equal(join.get(LibraryImpl_.libraryId), libraryId));
    return builder.getResultList();
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

    Date date = Date.from(ZonedDateTime.parse("2016-07-07T13:31:01Z").toInstant());

    assertEquals(LimsUtils.formatDate(date), LimsUtils.formatDate(cl.getTime()));
  }

}
