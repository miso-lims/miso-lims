package uk.ac.bbsrc.tgac.miso.sqlstore;

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
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePlatformDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencerReferenceDao;

public class SQLSequencerReferenceDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private JdbcTemplate template;

  @Mock
  private HibernatePlatformDao platformDAO;

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateSequencerReferenceDao dao;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(template);
    dao.setSessionFactory(sessionFactory);
    dao.setJdbcTemplate(template);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<SequencerReference> sequencers = dao.listAll();
    assertEquals(sequencers.iterator().next().getPlatform().getPlatformType(), PlatformType.ILLUMINA);
  }

  @Test
  public void testSaveNew() throws Exception {
    String serialNumber = "F00";
    Platform platform = dao.get(1L).getPlatform();
    InetAddress address = Inet4Address.getLoopbackAddress();
    SequencerReference seqref = new SequencerReferenceImpl("foo", address.getHostAddress(), platform);
    seqref.setSerialNumber(serialNumber);

    int sizeBefore = dao.listAll().size();
    long id = dao.save(seqref);
    SequencerReference retrieved = dao.get(id);
    assertEquals("did not insert sequencer refence", sizeBefore + 1, dao.listAll().size());
    assertEquals("sequencer reference name does not match", "foo", retrieved.getName());
    assertEquals("sequencer reference address does not match", address.getHostAddress(), retrieved.getIpAddress());
    assertEquals("sequencer reference date decommissioned does not match", null, retrieved.getDateDecommissioned());
    assertEquals("sequencer reference platform does not match", platform.getId(), retrieved.getPlatform().getId());

    assertTrue(dao.remove(retrieved));
    assertNull(dao.get(id));
  }

  @Test
  public void testSaveExisting() throws Exception {

    SequencerReference seqref = dao.get(2);
    seqref.setName("blargh");

    int sizeBefore = dao.listAll().size();
    long id = dao.save(seqref);
    SequencerReference retrieved = dao.get(id);
    assertEquals("sequencer reference name does not match", "blargh", retrieved.getName());
    assertEquals("did not update sample", sizeBefore, dao.listAll().size());
  }

  @Test
  public void testCount() throws Exception {
    int total = dao.count();
    assertEquals(2, total);
  }

  @Test
  public void testGet() throws Exception {
    SequencerReference seqref = dao.get(1);
    assertNotNull(seqref);
    assertEquals("seqref name does not match", "h1179", seqref.getName());
    assertNull("seqref date commissioned is not null", seqref.getDateCommissioned());
    assertNull("seqref date decommissioned is not null", seqref.getDateDecommissioned());
  }
}
