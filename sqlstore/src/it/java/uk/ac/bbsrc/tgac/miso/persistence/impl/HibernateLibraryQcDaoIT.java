/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

/**
 * @author Chris Salt
 *
 */
public class HibernateLibraryQcDaoIT extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateLibraryQcDao dao;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#create(uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC)}.
   * 
   * @throws IOException
   */
  @Test
  public void testSave() throws IOException {
    LibraryQC qc = new LibraryQC();
    Library library = (Library) currentSession().get(LibraryImpl.class, 3L);
    QcType qcType = (QcType) currentSession().get(QcType.class, 1L);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    qc.setLibrary(library);
    qc.setType(qcType);
    qc.setResults(new BigDecimal("12"));
    qc.setCreator(user);
    qc.setCreationTime(new Date());
    qc.setLastModified(new Date());
    long id = dao.save(qc);

    clearSession();

    LibraryQC saved = (LibraryQC) currentSession().get(LibraryQC.class, id);
    assertNotNull(saved);
    assertEquals(qc.getLibrary().getId(), saved.getLibrary().getId());
    assertEquals(qc.getType().getId(), saved.getType().getId());
    assertEquals(qc.getResults().compareTo(saved.getResults()), 0);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#get(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGet() throws IOException {
    LibraryQC qc = dao.get(1L);
    assertNotNull(qc);
    assertEquals("admin", qc.getCreator().getLoginName());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#listByLibraryId(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testListByLibraryId() throws IOException {
    List<LibraryQC> libraryQcs = (List<LibraryQC>) dao.listForEntity(1L);
    assertNotNull(libraryQcs);
    assertEquals(1, libraryQcs.size());
  }
}
