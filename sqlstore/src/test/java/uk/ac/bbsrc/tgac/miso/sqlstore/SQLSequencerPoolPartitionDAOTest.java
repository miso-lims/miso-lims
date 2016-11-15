package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;

public class SQLSequencerPoolPartitionDAOTest extends AbstractDAOTest {

  private static final Pool POOL = new PoolImpl();
  private static final SecurityProfile SECURITY_PROFILE = new SecurityProfile();
  static {
    POOL.setId(1L);
    SECURITY_PROFILE.setProfileId(1L);
  }

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private SequencerPartitionContainerStore sequencerPartitionContainerDAO;
  @Mock
  private PoolStore poolDAO;
  @Mock
  private Store<SecurityProfile> securityProfileDAO;


  @InjectMocks
  private SQLSequencerPoolPartitionDAO dao;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setPoolDAO(poolDAO);
    dao.setSecurityProfileDAO(securityProfileDAO);
    dao.setCascadeType(null);
    dao.setDataObjectFactory(new TgacDataObjectFactory());
    Mockito.when(poolDAO.get(1L)).thenReturn(POOL);
    Mockito.when(poolDAO.save(POOL)).thenReturn(1L);
    Mockito.when(securityProfileDAO.save(Matchers.any(SecurityProfile.class))).thenReturn(1L);
    Mockito.when(securityProfileDAO.get(Matchers.anyLong())).thenReturn(SECURITY_PROFILE);
  }

  @Test
  public void testCountAndList() throws IOException {
    assertTrue(dao.count() == dao.listAll().size());
  }

  @Test
  public void testListByPool() throws IOException {
    assertTrue(dao.listByPoolId(1L).size() > 0);
  }

  @Test
  public void testListBySequencerPartitionContainer() throws IOException {
    assertTrue(dao.listBySequencerPartitionContainerId(1L).size() > 0);
  }

  @Test
  public void testListBySubmission() throws IOException {
    assertTrue(dao.listBySubmissionId(3L).size() > 0);
  }

  @Test
  public void testGet() throws IOException {
    SequencerPoolPartition spp = dao.get(1L);
    assertTrue(spp.getPartitionNumber() == 1);
    assertEquals(POOL, spp.getPool());
  }

  @Test
  public void testSaveNew() throws IOException {
    SequencerPoolPartition spp = new PartitionImpl();
    spp.setPartitionNumber(1);
    spp.setPool(POOL);
    spp.setSecurityProfile(SECURITY_PROFILE);
    assertTrue(dao.save(spp) > 0);
  }

  @Test
  public void testSaveExisting() throws IOException {
    SequencerPoolPartition spp = dao.get(1L);
    spp.setPartitionNumber(2);
    assertTrue(dao.save(spp) > 0);
    SequencerPoolPartition reloadedSpp = dao.get(1L);
    assertTrue(reloadedSpp.getPartitionNumber() == 2);
  }

  @Test
  public void testRemove() throws IOException {
    int original = dao.count();
    dao.remove(dao.get(1L));
    assertTrue(original - dao.count() == 1);
  }

}
