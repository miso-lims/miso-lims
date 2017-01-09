/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
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
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.core.store.StatusStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateRunDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateChangeLogDao;

public class SQLRunDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private SecurityStore securityDAO;
  @Mock
  private Store<SecurityProfile> securityProfileDAO;
  @Mock
  private SequencerReferenceStore sequencerReferenceDAO;
  @Mock
  private RunQcStore runQcDAO;
  @Mock
  private SequencerPartitionContainerStore sequencerPartitionContainerDAO;
  @Mock
  private StatusStore statusDAO;
  @Mock
  private HibernateChangeLogDao changeLogDAO;

  @InjectMocks
  private HibernateRunDao dao;

  // Auto-increment sequence doesn't roll back with transactions, so must be tracked
  private static long nextAutoIncrementId = 5L;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListAll() throws IOException {
    List<Run> runs = dao.listAll();
    assertEquals(4, runs.size());
  }

  @Test
  public void testListAllWithLimit() throws IOException {
    List<Run> runs = dao.listAllWithLimit(2L);
    assertEquals(2, runs.size());
  }

  @Test
  public void testListAllWithBiggerLimit() throws IOException {
    List<Run> runs = dao.listAllWithLimit(50L);
    assertEquals(4, runs.size());
  }

  @Test
  public void testListAllWithZeroLimit() throws IOException {
    List<Run> runs = dao.listAllWithLimit(0L);
    assertEquals(4, runs.size());
  }

  @Test
  public void testListAllWithNegativeLimit() throws IOException {
    List<Run> runs = dao.listAllWithLimit(-1L);
    assertEquals(4, runs.size());
  }

  @Test
  public void testRunCount() throws IOException {
    assertEquals(4, dao.count());
  }

  @Test
  public void testListBySearch1204() throws IOException {
    List<Run> runs = dao.listBySearch("1204");
    assertEquals(2, runs.size());
  }

  @Test
  public void testListBySearchH1179() throws IOException {
    List<Run> runs = dao.listBySearch("h1179");
    assertEquals(4, runs.size());
  }

  @Test
  public void testListBySearchNone() throws IOException {
    List<Run> runs = dao.listBySearch("pizza");
    assertEquals(0, runs.size());
  }

  @Test
  public void testListBySearchEmpty() throws IOException {
    List<Run> runs = dao.listBySearch("");
    assertEquals(4, runs.size());
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
  public void testListByPlatformId() throws IOException {
    List<Run> runs = dao.listByPlatformId(16L);
    assertEquals(4, runs.size());
  }

  @Test
  public void testListByPlatformIdNone() throws IOException {
    List<Run> runs = dao.listByPlatformId(9999L);
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
    List<Run> runs = dao.listByPoolId(1);
    assertEquals(1, runs.size());
  }

  @Test
  public void testListByPoolIdNone() throws IOException {
    List<Run> runs = dao.listByPoolId(9999);
    assertEquals(0, runs.size());
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
  public void testLazyGet() throws IOException {
    mockNonLazyThings();
    Run run = dao.lazyGet(1L);
    assertNotNull(run);
    assertTrue(run.getSequencerPartitionContainers().isEmpty());
    assertTrue(run.getRunQCs().isEmpty());
    assertTrue(run.getNotes().isEmpty());
  }

  @Test
  public void testLazyGetNone() throws IOException {
    mockNonLazyThings();
    Run run = dao.lazyGet(-9999L);
    assertNull(run);
  }

  @Test
  public void testRemove() throws IOException, MisoNamingException {
    Run run = new RunImpl();
    String runName = "RUN111";
    run.setName(runName);
    run.setAlias("RunAlias");
    run.setDescription("Run Description");
    run.setPairedEnd(true);
    run.setPlatformType(PlatformType.ILLUMINA);
    SequencerReference mockSR = Mockito.mock(SequencerReference.class);
    when(mockSR.getId()).thenReturn(1L);
    run.setSequencerReference(mockSR);
    User mockUser = Mockito.mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);
    run.setLastModifier(mockUser);

    mockAutoIncrement(nextAutoIncrementId);

    long runId = dao.save(run);
    Run insertedRun = dao.get(runId);
    assertNotNull(insertedRun);
    assertTrue(dao.remove(insertedRun));
    Mockito.verify(changeLogDAO, Mockito.times(1)).deleteAllById("Run", run.getId());
    assertNull(dao.get(insertedRun.getId()));
    nextAutoIncrementId++;
  }

  @Test
  public void testSaveEdit() throws IOException, MisoNamingException {
    Run run = dao.get(1L);

    SecurityProfile profile = Mockito.mock(SecurityProfile.class);
    run.setSecurityProfile(profile);
    SequencerReference sequencer = Mockito.mock(SequencerReference.class);
    Mockito.when(sequencer.getId()).thenReturn(1L);
    User user = Mockito.mock(User.class);
    Mockito.when(user.getUserId()).thenReturn(1L);
    run.setSequencerReference(sequencer);
    run.setFilePath("/far/far/away");
    run.setName("AwesomeRun");
    run.setLastModifier(user);
    run.setSequencingParameters(null);

    assertEquals(1L, dao.save(run));
    Run savedRun = dao.get(1L);
    assertNotSame(run, savedRun);
    assertEquals(run.getId(), savedRun.getId());
    assertEquals("/far/far/away", savedRun.getFilePath());
    assertEquals("AwesomeRun", savedRun.getName());
  }

  @Test
  public void testSaveNew() throws IOException, MisoNamingException {
    assertNull(dao.get(nextAutoIncrementId));
    Run newRun = makeRun("TestRun");
    mockAutoIncrement(nextAutoIncrementId);

    assertEquals(nextAutoIncrementId, dao.save(newRun));

    Run savedRun = dao.get(nextAutoIncrementId);
    assertEquals(newRun.getAlias(), savedRun.getAlias());
    nextAutoIncrementId++;
  }

  @Test
  public void testSaveNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.save(null);
  }

  @Test
  public void testSaveAll() throws MisoNamingException, IOException {
    long autoIncrementId = nextAutoIncrementId;
    List<Run> runs = new ArrayList<>();
    Run run1 = makeRun("TestRun1");
    Run run2 = makeRun("TestRun2");
    runs.add(run1);
    runs.add(run2);
    mockAutoIncrement(autoIncrementId);

    assertNull(dao.get(autoIncrementId));
    assertNull(dao.get(autoIncrementId + 1L));

    dao.saveAll(runs);

    Run savedRun1 = dao.get(autoIncrementId);
    assertNotNull(savedRun1);
    assertEquals(run1.getAlias(), savedRun1.getAlias());

    Run savedRun2 = dao.get(autoIncrementId + 1L);
    assertNotNull(savedRun2);
    assertEquals(run2.getAlias(), savedRun2.getAlias());
    nextAutoIncrementId += 2;
  }

  @Test
  public void testSaveAllNone() throws IOException {
    mockAutoIncrement(5L);

    List<Run> runs = new ArrayList<>();
    int[] ids = dao.saveAll(runs);
    assertEquals(1, ids.length);
    assertEquals(AbstractRun.UNSAVED_ID, Long.valueOf(ids[0]));
  }

  @Test
  public void testSaveAllNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.saveAll(null);
  }

  private Run makeRun(String alias) {
    SecurityProfile profile = Mockito.mock(SecurityProfile.class);
    SequencerReference sequencer = Mockito.mock(SequencerReference.class);
    Mockito.when(sequencer.getId()).thenReturn(1L);
    User user = Mockito.mock(User.class);
    Mockito.when(user.getUserId()).thenReturn(1L);
    Run run = new RunImpl();
    run.setSecurityProfile(profile);
    run.setAlias(alias);
    run.setDescription("description");
    run.setPlatformRunId(1234);
    run.setPairedEnd(true);
    run.setCycles(250);
    run.setFilePath("/somewhere/someplace/");
    run.setPlatformType(PlatformType.ILLUMINA);
    run.setSequencerReference(sequencer);
    run.setLastModifier(user);
    return run;
  }

  private void mockAutoIncrement(long value) {
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
  }

  @SuppressWarnings("unchecked") // Safe (for mocks in a unit test)
  private void mockNonLazyThings() throws IOException {
    User mockUser = Mockito.mock(User.class);
    Mockito.when(securityDAO.getUserById(Matchers.anyLong())).thenReturn(mockUser);

    List<SequencerPartitionContainer<SequencerPoolPartition>> mockContainers = new ArrayList<>();
    mockContainers.add(Mockito.mock(SequencerPartitionContainer.class));
    Mockito.when(sequencerPartitionContainerDAO.listAllSequencerPartitionContainersByRunId(Matchers.anyLong())).thenReturn(mockContainers);

    List<RunQC> mockQcs = new ArrayList<>();
    mockQcs.add(Mockito.mock(RunQC.class));
    Mockito.when(runQcDAO.listByRunId(Matchers.anyLong())).thenReturn(mockQcs);
  }

  private void assertNonLazyThings(Run run) {
    assertNotNull(run);
    assertFalse(run.getSequencerPartitionContainers().isEmpty());
    assertFalse(run.getRunQCs().isEmpty());
    assertFalse(run.getNotes().isEmpty());
  }

  @Test
  public void testListWithLimitAndOffset() throws IOException {
    List<Run> runs = dao.listByOffsetAndNumResults(2, 2, "asc", "id");
    assertEquals(2, runs.size());
    assertEquals(3L, runs.get(0).getId());
  }

  @Test
  public void testCountBySearch() throws IOException {
    assertEquals(2, dao.countBySearch("1204"));
  }

  @Test
  public void testCountByEmptySearch() throws IOException {
    assertEquals(4L, dao.countBySearch(""));
  }

  @Test
  public void testCountByBadSearch() throws IOException {
    assertEquals(0L, dao.countBySearch("; DROP TABLE Run;"));
  }

  @Test
  public void testListBySearchWithLimit() throws IOException {
    List<Run> runs = dao.listBySearchOffsetAndNumResults(2, 2, "C0", "asc", "id");
    assertEquals(1, runs.size());
    assertEquals(4L, runs.get(0).getId());
  }

  @Test
  public void testListByEmptySearchWithLimit() throws IOException {
    List<Run> runs = dao.listBySearchOffsetAndNumResults(0, 3, "", "asc", "id");
    assertEquals(3L, runs.size());
  }

  @Test
  public void testListByBadSearchWithLimit() throws IOException {
    List<Run> runs = dao.listBySearchOffsetAndNumResults(0, 2, "; DROP TABLE Run;", "asc", "id");
    assertEquals(0L, runs.size());
  }

  @Test
  public void testListByOffsetBadSortDir() throws IOException {
    List<Run> runs = dao.listByOffsetAndNumResults(1, 3, "BARK", "id");
    assertEquals(3, runs.size());
  }

  @Test
  public void testListOffsetBadLimit() throws IOException {
    exception.expect(IOException.class);
    dao.listByOffsetAndNumResults(5, -3, "asc", "id");
  }

  @Test
  public void testListOffsetThreeWithThreeSamplesPerPageOrderLastMod() throws IOException {
    List<Run> runs = dao.listByOffsetAndNumResults(2, 2, "desc", "lastModified");
    assertEquals(2, runs.size());
    assertEquals(2, runs.get(0).getId());
  }

  @Test
  public void testWatchers() throws Exception {
    Run run = dao.get(1L);
    assertNotNull(run);
    assertEquals(0, run.getWatchers().size());

    User user = (User) sessionFactory.getCurrentSession().get(UserImpl.class, 1L);
    assertNotNull(user);

    dao.addWatcher(run, user);
    assertEquals(1, run.getWatchers().size());
    run = dao.get(1L);
    assertEquals(1, run.getWatchers().size());

    dao.removeWatcher(run, user);
    assertEquals(0, run.getWatchers().size());
    run = dao.get(1L);
    assertEquals(0, run.getWatchers().size());
  }
}
