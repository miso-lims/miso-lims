package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
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
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public class HibernatePoolQcDaoIT extends AbstractDAOTest {
  
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  
  @Autowired
  private SessionFactory sessionFactory;
  
  @InjectMocks
  private HibernatePoolQCDao dao;
  
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
    assertEquals("admin", qc.getCreator().getLoginName());
    assertTrue(new BigDecimal("12.3").compareTo(qc.getResults()) == 0);
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
    PoolQC qc = new PoolQC();
    Pool pool = (Pool) currentSession().get(PoolImpl.class, 1L);
    QcType qcType = (QcType) currentSession().get(QcType.class, 8L);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    qc.setPool(pool);
    qc.setCreator(user);
    qc.setDate(new Date());
    qc.setType(qcType);
    qc.setResults(new BigDecimal("12"));
    qc.setCreationTime(new Date());
    qc.setLastModified(new Date());
    long id = dao.save(qc);

    clearSession();

    PoolQC saved = (PoolQC) currentSession().get(PoolQC.class, id);
    assertNotNull(saved);
    assertEquals(qc.getPool().getId(), saved.getPool().getId());
    assertEquals(qc.getType().getId(), saved.getType().getId());
    assertEquals(qc.getResults().compareTo(saved.getResults()), 0);
  }
  
  @Test
  public void testSaveUpdate() throws IOException {
    PoolQC qc = dao.get(1L);
    assertNotNull(qc);
    User mockUser = new UserImpl();
    mockUser.setId(1L);

    qc.setCreator(mockUser);
    qc.setResults(new BigDecimal("99.99"));
    Pool pool = new PoolImpl();
    pool.setId(1L);
    qc.setPool(pool);
    QcType type = new QcType();
    type.setId(101L);
    qc.setType(type);
    assertEquals(1L, dao.save(qc));
    PoolQC saved = dao.get(1L);
    assertEquals(qc.getCreator(), saved.getCreator());
    assertEquals(qc.getResults(), saved.getResults());
  }
  
}
