package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
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
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class HibernateInstrumentDaoTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private JdbcTemplate template;

  @Mock
  private HibernatePlatformDao platformDAO;

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateInstrumentDao dao;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(template);
    dao.setSessionFactory(sessionFactory);
    dao.setJdbcTemplate(template);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<Instrument> instruments = dao.listAll();
    assertEquals(PlatformType.ILLUMINA, instruments.iterator().next().getPlatform().getPlatformType());
  }

  @Test
  public void testSaveNew() throws Exception {
    String serialNumber = "F00";
    Platform platform = dao.get(1L).getPlatform();
    InetAddress address = Inet4Address.getLoopbackAddress();
    Instrument instrument = new InstrumentImpl("foo", address.getHostAddress(), platform);
    instrument.setSerialNumber(serialNumber);

    int sizeBefore = dao.listAll().size();
    long id = dao.save(instrument);
    Instrument retrieved = dao.get(id);
    assertEquals("did not insert instrument", sizeBefore + 1, dao.listAll().size());
    assertEquals("instrument name does not match", "foo", retrieved.getName());
    assertEquals("instrument address does not match", address.getHostAddress(), retrieved.getIpAddress());
    assertEquals("instrument date decommissioned does not match", null, retrieved.getDateDecommissioned());
    assertEquals("instrument platform does not match", platform.getId(), retrieved.getPlatform().getId());

    assertTrue(dao.remove(retrieved));
    assertNull(dao.get(id));
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
  public void testCount() throws Exception {
    int total = dao.count();
    assertEquals(2, total);
  }

  @Test
  public void testGet() throws Exception {
    Instrument instrument = dao.get(1);
    assertNotNull(instrument);
    assertEquals("instrument name does not match", "SN7001179", instrument.getName());
    assertNull("instrument date commissioned is not null", instrument.getDateCommissioned());
    assertNull("instrument date decommissioned is not null", instrument.getDateDecommissioned());
  }
}
