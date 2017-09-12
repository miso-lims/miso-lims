/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;

/**
 * @author Chris Salt
 *
 */
public class HibernateLibraryQcDaoTest extends AbstractDAOTest {

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
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#save(uk.ac.bbsrc.tgac.miso.core.data.LibraryQC)}.
   * 
   * @throws IOException
   * @throws MalformedLibraryException
   */
  @Test
  public void testSave() throws IOException, MalformedLibraryException {
    LibraryQC qc = new LibraryQC();
    Library library = new LibraryImpl();
    library.setId(3L);
    qc.setLibrary(library);
    qc.setType(new QcType());
    qc.getType().setQcTypeId(1L);
    qc.setCreator(new UserImpl());
    qc.getCreator().setUserId(1L);
    long id = dao.save(qc);

    LibraryQC saved = (LibraryQC) dao.get(id);
    assertEquals(qc, saved);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#get(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGet() throws IOException {
    LibraryQC qc = (LibraryQC) dao.get(1L);
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
    @SuppressWarnings("unchecked")
    List<LibraryQC> libraryQcs = (List<LibraryQC>) dao.listForEntity(1L);
    assertNotNull(libraryQcs);
    assertEquals(1, libraryQcs.size());
  }
}
