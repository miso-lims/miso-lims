package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolException;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePoolQCDao;

public class SQLPoolQCDAOTest extends AbstractDAOTest {
  
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  
  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;
  
  @Mock
  private SessionFactory sessionFactory;
  
  @InjectMocks
  private HibernatePoolQCDao dao;
  
  //Auto-increment sequence doesn't roll back with transactions, so must be tracked
   private static long nextAutoIncrementId = 4L;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }
  
  @Test
  public void testGet() throws IOException {
    PoolQC qc = dao.get(1L);
    assertNotNull(qc);
    assertEquals(1L, qc.getId());
    assertEquals("person", qc.getQcCreator());
    assertEquals(Double.valueOf(12.3D), qc.getResults());
    Calendar cal = Calendar.getInstance(); 
    cal.set(2016, 2, 18, 0, 0, 0);
    Calendar qcCal = Calendar.getInstance();
    qcCal.setTime(qc.getQcDate());
    assertEquals(cal.get(Calendar.YEAR), qcCal.get(Calendar.YEAR));
    assertEquals(cal.get(Calendar.MONTH), qcCal.get(Calendar.MONTH));
    assertEquals(cal.get(Calendar.DAY_OF_MONTH), qcCal.get(Calendar.DAY_OF_MONTH));
  }
  
  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(100L));
  }
  
  @Test
  public void testListAll() throws IOException {
    assertEquals(3, dao.listAll().size());
  }
  
  @Test
  public void testCount() throws IOException {
    assertEquals(3, dao.count());
  }
  
  @Test
  public void testListByPoolId() throws IOException {
    assertEquals(2, dao.listByPoolId(1L).size());
  }
  
  @Test
  public void testListAllPoolQcTypes() throws IOException {
    assertEquals(4, dao.listAllPoolQcTypes().size());
  }
  
  @Test
  public void testGetPoolQcTypeById() throws IOException {
    assertNotNull(dao.getPoolQcTypeById(8L));
  }
  
  @Test
  public void testGetPoolQcTypeByIdNone() throws IOException {
    assertNull(dao.getPoolQcTypeById(100L));
  }
  
  @Test
  public void testGetPoolQcTypeByName() throws IOException {
    assertNotNull(dao.getPoolQcTypeByName("poolQcType1"));
  }
  
  @Test
  public void testGetPoolQcTypeByNameNone() throws IOException {
    assertNull(dao.getPoolQcTypeByName("poolQcType100"));
  }
  
  @Test
  public void testGetPoolQcTypeByNameNull() throws IOException {
    assertNull(dao.getPoolQcTypeByName(null));
  }
  
  @Test
  public void testRemove() throws IOException {
    PoolQC qc = dao.get(1L);
    assertNotNull(qc);
    assertTrue(dao.remove(qc));
    assertNull(dao.get(1L));
  }
  
  @Test
  public void testSaveNew() throws IOException, MalformedPoolException {
    long autoIncrementId = nextAutoIncrementId;
    PoolQC qc = new PoolQCImpl();
    Pool pool = new PoolImpl();
    pool.setId(1L);
    qc.setPool(pool);
    qc.setQcCreator("me");
    qc.setQcDate(new Date());
    QcType type = new QcType();
    type.setQcTypeId(8L);
    qc.setQcType(type);
    assertNull(dao.get(autoIncrementId));
    assertEquals(autoIncrementId, dao.save(qc));
    PoolQC saved = dao.get(autoIncrementId);
    nextAutoIncrementId++;
    assertEquals(qc.getQcCreator(), saved.getQcCreator());
    assertEquals(qc.getQcType().getQcTypeId(), saved.getQcType().getQcTypeId());
  }
  
  @Test
  public void testSaveUpdate() throws IOException, MalformedPoolException {
    PoolQC qc = dao.get(1L);
    assertNotNull(qc);
    qc.setQcCreator("me");
    qc.setResults(99.99);
    Pool pool = new PoolImpl();
    pool.setId(1L);
    qc.setPool(pool);
    QcType type = new QcType();
    type.setQcTypeId(101L);
    qc.setQcType(type);
    assertEquals(1L, dao.save(qc));
    PoolQC saved = dao.get(1L);
    assertEquals(qc.getQcCreator(), saved.getQcCreator());
    assertEquals(qc.getResults(), saved.getResults());
  }
  
}
