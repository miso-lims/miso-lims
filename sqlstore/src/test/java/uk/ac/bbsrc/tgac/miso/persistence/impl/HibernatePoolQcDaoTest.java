package uk.ac.bbsrc.tgac.miso.persistence.impl;

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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public class HibernatePoolQcDaoTest extends AbstractDAOTest {
  
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  
  @Autowired
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
    PoolQC qc = (PoolQC) dao.get(1L);
    assertNotNull(qc);
    assertEquals(1L, qc.getId());
    assertEquals("admin", qc.getCreator().getLoginName());
    assertEquals(Double.valueOf(12.3D), qc.getResults());
    Calendar cal = Calendar.getInstance(); 
    cal.set(2016, 2, 18, 0, 0, 0);
    Calendar qcCal = Calendar.getInstance();
    qcCal.setTime(qc.getDate());
    assertEquals(cal.get(Calendar.YEAR), qcCal.get(Calendar.YEAR));
    assertEquals(cal.get(Calendar.MONTH), qcCal.get(Calendar.MONTH));
    assertEquals(cal.get(Calendar.DAY_OF_MONTH), qcCal.get(Calendar.DAY_OF_MONTH));
  }
  
  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(100L));
  }
  
  
  
  @Test
  public void testSaveNew() throws IOException {
    long autoIncrementId = nextAutoIncrementId;
    PoolQC qc = new PoolQC();
    Pool pool = new PoolImpl();
    pool.setId(1L);
    qc.setPool(pool);
    User mockUser = new UserImpl();
    mockUser.setUserId(1L);
    qc.setCreator(mockUser);
    qc.setDate(new Date());
    QcType type = new QcType();
    type.setQcTypeId(8L);
    qc.setType(type);
    assertNull(dao.get(autoIncrementId));
    assertEquals(autoIncrementId, dao.save(qc));
    PoolQC saved = (PoolQC) dao.get(autoIncrementId);
    nextAutoIncrementId++;
    assertEquals(qc.getCreator(), saved.getCreator());
    assertEquals(qc.getType().getQcTypeId(), saved.getType().getQcTypeId());
  }
  
  @Test
  public void testSaveUpdate() throws IOException {
    PoolQC qc = (PoolQC) dao.get(1L);
    assertNotNull(qc);
    User mockUser = new UserImpl();
    mockUser.setUserId(1L);

    qc.setCreator(mockUser);
    qc.setResults(99.99);
    Pool pool = new PoolImpl();
    pool.setId(1L);
    qc.setPool(pool);
    QcType type = new QcType();
    type.setQcTypeId(101L);
    qc.setType(type);
    assertEquals(1L, dao.save(qc));
    PoolQC saved = (PoolQC) dao.get(1L);
    assertEquals(qc.getCreator(), saved.getCreator());
    assertEquals(qc.getResults(), saved.getResults());
  }
  
}
