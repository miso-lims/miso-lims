/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryQcDao;

/**
 * @author Chris Salt
 *
 */
public class SQLLibraryQCDAOTest extends AbstractDAOTest {

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
    LibraryQC qc = new LibraryQCImpl();
    Library library = new LibraryImpl();
    library.setId(3L);
    qc.setLibrary(library);
    qc.setQcType(Mockito.mock(QcType.class));
    long id = dao.save(qc);

    LibraryQC saved = dao.get(id);
    assertEquals(qc, saved);
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
    assertEquals("admin", qc.getQcCreator());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#listByLibraryId(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testListByLibraryId() throws IOException {
    List<LibraryQC> libraryQcs = (List<LibraryQC>) dao.listByLibraryId(1L);
    assertNotNull(libraryQcs);
    assertEquals(1, libraryQcs.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#listAll()}.
   * 
   * @throws IOException
   */
  @Test
  public void testListAll() throws IOException {
    List<LibraryQC> libraryQcs = (List<LibraryQC>) dao.listAll();
    assertNotNull(libraryQcs);
    assertTrue(libraryQcs.size() > 0);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#count()}.
   * 
   * @throws IOException
   */
  @Test
  public void testCount() throws IOException {
    int count = dao.count();
    assertTrue(count > 0);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#remove(uk.ac.bbsrc.tgac.miso.core.data.LibraryQC)}.
   * 
   * @throws IOException
   */
  @Test
  public void testRemove() throws IOException {
    LibraryQC libraryQc = dao.get(1L);
    assertNotNull(libraryQc);
    dao.remove(libraryQc);
    LibraryQC qc = dao.get(1L);
    assertNull(qc);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#listAllLibraryQcTypes()}.
   * 
   * @throws IOException
   */
  @Test
  public void testListAllLibraryQcTypes() throws IOException {
    List<QcType> types = (List<QcType>) dao.listAllLibraryQcTypes();
    assertNotNull(types);
    assertTrue(types.size() > 0);

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#getLibraryQcTypeById(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGetLibraryQcTypeById() throws IOException {
    QcType type = dao.getLibraryQcTypeById(1L);
    assertEquals("qPCR", type.getName());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO#getLibraryQcTypeByName(java.lang.String)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGetLibraryQcTypeByName() throws IOException {
    QcType type = dao.getLibraryQcTypeByName("qPCR");
    assertEquals(new Long(1), type.getQcTypeId());
  }

}
