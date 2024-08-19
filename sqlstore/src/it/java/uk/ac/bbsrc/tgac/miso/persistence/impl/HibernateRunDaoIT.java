package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentStore;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;
import uk.ac.bbsrc.tgac.miso.persistence.SequencerPartitionContainerStore;

public class HibernateRunDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @PersistenceContext
  private EntityManager entityManager;

  @Mock
  private SecurityStore securityDAO;
  @Mock
  private InstrumentStore instrumentDAO;
  @Mock
  private SequencerPartitionContainerStore sequencerPartitionContainerDAO;
  @Mock
  private HibernateChangeLogDao changeLogDAO;

  @InjectMocks
  private HibernateRunDao dao;

  private final User emptyUser = new UserImpl();
  private final Instrument emptySR = new InstrumentImpl();

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setEntityManager(entityManager);
    emptyUser.setId(1L);
    when(securityDAO.getUserById(ArgumentMatchers.anyLong())).thenReturn(emptyUser);
    emptySR.setId(1L);
    when(instrumentDAO.get(ArgumentMatchers.anyLong())).thenReturn(emptySR);
  }

  @Test
  public void testListByProjectId() throws IOException {
    List<Run> runs = dao.listByProjectId(1L);
    assertEquals(1, runs.size());
  }

  @Test
  public void testListByProjectIdNone() throws IOException {
    List<Run> runs = dao.listByProjectId(9999L);
    assertEquals(0, runs.size());
  }

  @Test
  public void testListByStatusCompleted() throws IOException {
    List<Run> runs = dao.listByStatus("Completed");
    assertEquals(3, runs.size());
  }

  @Test
  public void testListByStatusEmpty() throws IOException {
    List<Run> runs = dao.listByStatus("");
    assertEquals(0, runs.size());
  }

  @Test
  public void testListByStatusNull() throws IOException {
    List<Run> runs = dao.listByStatus(null);
    assertEquals(0, runs.size());
  }

  @Test
  public void testListByPoolId() throws IOException {
    List<Run> runs = dao.listByPoolId(1L);
    assertEquals(1, runs.size());
    assertEquals(1L, runs.get(0).getId());
  }

  @Test
  public void testListByPoolIdNone() throws IOException {
    List<Run> runs = dao.listByPoolId(9999L);
    assertEquals(0, runs.size());
  }

  @Test
  public void testListByLibraryAliquotId() throws IOException {
    List<Run> runs = dao.listByLibraryAliquotId(3L);
    assertEquals(1, runs.size());
    assertEquals(1L, runs.get(0).getId());
  }

  @Test
  public void testListByLibraryAliquotIdNone() throws IOException {
    List<Run> runs = dao.listByLibraryAliquotId(9999L);
    assertEquals(0, runs.size());
  }

  @Test
  public void testListByLibraryIdList() throws IOException {
    List<Run> runs = dao.listByLibraryIdList(Arrays.asList(3L));
    assertEquals(1, runs.size());
    assertEquals(1L, runs.get(0).getId());
  }

  @Test
  public void testListByLibraryIdListNone() throws IOException {
    List<Run> runs = dao.listByLibraryIdList(Arrays.asList(9999L));
    assertEquals(0, runs.size());
  }

  @Test
  public void testListByIdsNull() throws Exception {
    assertEquals(0, dao.listByIdList(null).size());
  }

  @Test
  public void testListByIdList() throws Exception {
    List<Long> ids = Arrays.asList(2L, 3L);
    List<Run> runs = dao.listByIdList(ids);
    assertEquals(2, runs.size());
    for (Long id : ids) {
      assertTrue(runs.stream().anyMatch(run -> run.getId() == id.longValue()));
    }
  }

  @Test
  public void testListBySequencerPartitionContainerId() throws IOException {
    List<Run> runs = dao.listBySequencerPartitionContainerId(1L);
    assertEquals(1, runs.size());
  }

  @Test
  public void testListBySequencerPartitionContainerIdNone() throws IOException {
    List<Run> runs = dao.listBySequencerPartitionContainerId(-9999L);
    assertEquals(0, runs.size());
  }

  @Test
  public void testGetLatestStartDateRunBySequencerPartitionContainerId() throws IOException {
    Run run = dao.getLatestStartDateRunBySequencerPartitionContainerId(1L);
    assertNotNull(run);
  }

  @Test
  public void testGetLatestStartDateRunBySequencerPartitionContainerIdNone() throws IOException {
    Run run = dao.getLatestStartDateRunBySequencerPartitionContainerId(-9999L);
    assertNull(run);
  }

  @Test
  public void testGetLatestRunIdRunBySequencerPartitionContainerId() throws IOException {
    Run run = dao.getLatestRunIdRunBySequencerPartitionContainerId(1L);
    assertNotNull(run);
  }

  @Test
  public void testGetLatestRunIdRunBySequencerPartitionContainerIdNone() throws IOException {
    Run run = dao.getLatestRunIdRunBySequencerPartitionContainerId(-9999L);
    assertNull(run);
  }

  @Test
  public void testGet() throws IOException {
    mockNonLazyThings();
    Run run = dao.get(1L);
    assertNonLazyThings(run);
  }

  @Test
  public void testGetByAlias() throws IOException {
    mockNonLazyThings();
    Run run = dao.getByAlias("120323_h1179_0070_BC0JHTACXX");
    assertNonLazyThings(run);
  }

  @Test
  public void testGetByAliasNone() throws IOException {
    Run run = dao.getByAlias("");
    assertNull(run);
  }

  @Test
  public void testGetByAliasNull() throws IOException {
    Run run = dao.getByAlias(null);
    assertNull(run);
  }

  @Test
  public void testSaveEdit() throws IOException, MisoNamingException {
    Run run = dao.get(1L);

    Instrument instrument = (Instrument) currentSession().get(InstrumentImpl.class, 1L);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    run.setSequencer(instrument);
    run.setFilePath("/far/far/away");
    run.setName("AwesomeRun");
    run.setLastModifier(user);
    run.setSequencingParameters(null);
    assertEquals(1L, dao.update(run));

    clearSession();

    Run savedRun = dao.get(1L);
    assertEquals(run.getId(), savedRun.getId());
    assertEquals("/far/far/away", savedRun.getFilePath());
    assertEquals("AwesomeRun", savedRun.getName());
  }

  @Test
  public void testSaveNew() throws IOException, MisoNamingException {
    Run newRun = makeRun("TestRun");
    newRun.setName("RUNX");
    Long savedId = dao.create(newRun);

    clearSession();

    Run savedRun = dao.get(savedId);
    assertEquals(newRun.getAlias(), savedRun.getAlias());
  }

  @Test
  public void testCreateNull() throws IOException {
    exception.expect(IllegalArgumentException.class);
    dao.create(null);
  }

  @Test
  public void testUpdateNull() throws IOException {
    exception.expect(IllegalArgumentException.class);
    dao.update(null);
  }

  private Run makeRun(String alias) {
    Instrument instrument = emptySR;
    Date now = new Date();
    User user = new UserImpl();
    user.setId(1L);

    Run run = new IlluminaRun();
    run.setAlias(alias);
    run.setDescription("description");
    run.setFilePath("/somewhere/someplace/");
    run.setSequencer(instrument);
    run.setCreator(user);
    run.setCreationTime(now);
    run.setLastModifier(user);
    run.setLastModified(now);
    return run;
  }

  private void mockNonLazyThings() throws IOException {
    User mockUser = Mockito.mock(UserImpl.class);
    Mockito.when(securityDAO.getUserById(ArgumentMatchers.anyLong())).thenReturn(mockUser);

    List<SequencerPartitionContainer> mockContainers = new ArrayList<>();
    mockContainers.add(Mockito.mock(SequencerPartitionContainerImpl.class));
    Mockito.when(sequencerPartitionContainerDAO.listAllSequencerPartitionContainersByRunId(ArgumentMatchers.anyLong()))
        .thenReturn(mockContainers);
  }

  private void assertNonLazyThings(Run run) {
    assertNotNull(run);
    assertFalse(run.getSequencerPartitionContainers().isEmpty());
  }

  @Test
  public void testListWithLimitAndOffset() throws IOException {
    List<Run> runs = dao.list(2, 2, true, "id");
    assertEquals(2, runs.size());
    assertEquals(3L, runs.get(0).getId());
  }

  @Test
  public void testListBySearchWithLimit() throws IOException {
    List<Run> runs = dao.list(2, 2, true, "id", PaginationFilter.query("*C0*"));
    assertEquals(1, runs.size());
    assertEquals(4L, runs.get(0).getId());
  }

  @Test
  public void testListByEmptySearchWithLimit() throws IOException {
    List<Run> runs = dao.list(0, 3, true, "id");
    assertEquals(3L, runs.size());
  }

  @Test
  public void testListByBadSearchWithLimit() throws IOException {
    List<Run> runs = dao.list(0, 2, true, "id", PaginationFilter.query("; DROP TABLE Run;"));
    assertEquals(0L, runs.size());
  }

  @Test
  public void testListOffsetBadLimit() throws IOException {
    exception.expect(IOException.class);
    dao.list(5, -3, true, "id");
  }

  @Test
  public void testListOffsetThreeWithThreeSamplesPerPageOrderLastMod() throws IOException {
    List<Run> runs = dao.list(2, 2, false, "lastModified");
    assertEquals(2, runs.size());
    assertEquals(2, runs.get(0).getId());
  }

}
