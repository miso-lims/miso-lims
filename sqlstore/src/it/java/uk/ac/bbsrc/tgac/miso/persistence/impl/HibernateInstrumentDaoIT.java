package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.annotations.VisibleForTesting;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ServiceRecordStore;

public class HibernateInstrumentDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Mock
  private HibernateInstrumentModelDao platformDAO;

  @Mock
  private ServiceRecordStore serviceRecordDao;

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateInstrumentDao dao;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<Instrument> instruments = dao.listAll();
    assertEquals(6, instruments.size());
  }

  @Test
  public void testListByType() throws Exception {
    assertEquals(4, dao.listByType(InstrumentType.SEQUENCER).size());
  }

  @Test
  public void testSaveExisting() throws Exception {

    Instrument instrument = dao.get(2);
    instrument.setName("blargh");

    int sizeBefore = dao.listAll().size();
    long id = dao.save(instrument);
    Instrument retrieved = dao.get(id);
    assertEquals("instrument name does not match", "blargh", retrieved.getName());
    assertEquals("did not update sample", sizeBefore, dao.listAll().size());
  }

  @Test
  public void testGet() throws Exception {
    Instrument instrument = dao.get(1);
    assertNotNull(instrument);
    assertEquals("instrument name does not match", "SN7001179", instrument.getName());
    assertNull("instrument date commissioned is not null", instrument.getDateCommissioned());
    assertNull("instrument date decommissioned is not null", instrument.getDateDecommissioned());
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("Sequencer"));
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter) throws IOException {
    // verify Hibernate mappings by ensuring that no exception is thrown
    assertNotNull(dao.list(0, 10, true, "name", filter));
  }

  @Test
  public void testGetUsageByRuns() throws Exception {
    Instrument instrument = (Instrument) currentSession().get(InstrumentImpl.class, 1L);
    assertEquals(3L, dao.getUsageByRuns(instrument));
  }

  @Test
  public void testGetUsageByArrayRuns() throws Exception {
    Instrument instrument = (Instrument) currentSession().get(InstrumentImpl.class, 3L);
    assertEquals(1L, dao.getUsageByArrayRuns(instrument));
  }

  @Test
  public void testGetUsageByQcs() throws Exception {
    Instrument instrument = (Instrument) currentSession().get(InstrumentImpl.class, 5L);
    assertEquals(1L, dao.getUsageByQcs(instrument));
  }

  @Test
  public void testGetUsageByQcsNone() throws Exception {
    Instrument instrument = (Instrument) currentSession().get(InstrumentImpl.class, 1L);
    assertEquals(0L, dao.getUsageByQcs(instrument));
  }

  @Test
  public void testGetByUpgradedInstrument() throws Exception {
    Instrument upgraded = dao.getByUpgradedInstrument(2L);
    assertNotNull(upgraded);
    assertEquals(6L, upgraded.getId());
  }

  @Test
  public void testGetByName() throws Exception {
    String name = "old hiseq";
    Instrument instrument = dao.getByName(name);
    assertNotNull(instrument);
    assertEquals(name, instrument.getName());
  }

  @Test
  public void testGetbyServiceRecord() throws Exception {
    ServiceRecord record = serviceRecordDao.get(1L);
    assertNotNull(record);
    Instrument instrument = dao.getByServiceRecord(record);
    assertNotNull(instrument);
    assertEquals(1L, instrument.getId());
  }

  @Test
  public void testCreate() throws Exception {
    Instrument instrument = new InstrumentImpl();
    instrument.setName("Test Instrument");
    InstrumentModel model = (InstrumentModel) currentSession().get(InstrumentModel.class, 2L);
    instrument.setInstrumentModel(model);
    long savedId = dao.save(instrument);

    clearSession();

    Instrument saved = (Instrument) currentSession().get(InstrumentImpl.class, savedId);
    assertNotNull(saved);
  }

}
