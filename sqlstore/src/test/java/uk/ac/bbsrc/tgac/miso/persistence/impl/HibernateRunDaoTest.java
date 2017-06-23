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

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateRunDaoTest extends AbstractDAOTest {

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
  private HibernateChangeLogDao changeLogDAO;


  @InjectMocks
  private HibernateRunDao dao;

  private final User emptyUser = new UserImpl();
  private final SequencerReference emptySR = new SequencerReferenceImpl();

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    dao.setSecurityStore(securityDAO);
    emptyUser.setUserId(1L);
    when(securityDAO.getUserById(Matchers.anyLong())).thenReturn(emptyUser);
    emptySR.setId(1L);
    when(sequencerReferenceDAO.get(Matchers.anyLong())).thenReturn(emptySR);
  }

  @Test
  public void testListAll() throws IOException {
    List<Run> runs = dao.listAll();
    assertTrue(runs.size() > 0);
  }

  @Test
  public void testListAllWithLimit() throws IOException {
    List<Run> runs = dao.listAllWithLimit(2L);
    assertEquals(2, runs.size());
  }

  @Test
  public void testListAllWithBiggerLimit() throws IOException {
    List<Run> runs = dao.listAllWithLimit(50L);
    assertTrue(runs.size() > 2);
  }

  @Test
  public void testListAllWithZeroLimit() throws IOException {
    List<Run> runs = dao.listAllWithLimit(0L);
    assertTrue(runs.size() > 0);
  }

  @Test
  public void testListAllWithNegativeLimit() throws IOException {
    List<Run> runs = dao.listAllWithLimit(-1L);
    assertTrue(runs.size() > 0);
  }

  @Test
  public void testRunCount() throws IOException {
    assertEquals(dao.listAll().size(), dao.count());
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
  public void testRemove() throws IOException, MisoNamingException {
    Run run = makeRun("RunAlias");

    long runId = dao.save(run);
    Run insertedRun = dao.get(runId);
    assertNotNull(insertedRun);

    runId = dao.save(insertedRun);

    Run changedRun = dao.get(runId);
    assertTrue(dao.remove(changedRun));
    assertNull(dao.get(changedRun.getId()));
  }

  @Test
  public void testSaveEdit() throws IOException, MisoNamingException {
    Run run = dao.get(1L);

    SecurityProfile profile = Mockito.mock(SecurityProfile.class);
    run.setSecurityProfile(profile);
    SequencerReference sequencer = Mockito.mock(SequencerReferenceImpl.class);
    Mockito.when(sequencer.getId()).thenReturn(1L);
    User user = Mockito.mock(UserImpl.class);
    Mockito.when(user.getUserId()).thenReturn(1L);
    run.setSequencerReference(sequencer);
    run.setFilePath("/far/far/away");
    run.setName("AwesomeRun");
    run.setLastModifier(user);
    run.setSequencingParameters(null);

    assertEquals(1L, dao.save(run));
    Run savedRun = dao.get(1L);
    assertSame(run, savedRun);
    assertEquals(run.getId(), savedRun.getId());
    assertEquals("/far/far/away", savedRun.getFilePath());
    assertEquals("AwesomeRun", savedRun.getName());
  }

  @Test
  public void testSaveNew() throws IOException, MisoNamingException {
    Run newRun = makeRun("TestRun");
    Long savedId = dao.save(newRun);

    Run savedRun = dao.get(savedId);
    assertEquals(newRun.getAlias(), savedRun.getAlias());
  }

  @Test
  public void testSaveNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.save(null);
  }

  @Test
  public void testSaveAll() throws MisoNamingException, IOException {
    List<Run> runs = new ArrayList<>();
    Run run1 = makeRun("TestRun1");
    Run run2 = makeRun("TestRun2");
    runs.add(run1);
    runs.add(run2);

    assertNull(dao.getByAlias(run1.getAlias()));
    assertNull(dao.getByAlias(run2.getAlias()));
    dao.saveAll(runs);

    Run savedRun1 = dao.getByAlias(run1.getAlias());
    assertNotNull(savedRun1);
    assertEquals(run1.getAlias(), savedRun1.getAlias());

    Run savedRun2 = dao.getByAlias(run2.getAlias());
    assertNotNull(savedRun2);
    assertEquals(run2.getAlias(), savedRun2.getAlias());
  }

  @Test
  public void testSaveAllNone() throws IOException {
    int originalCount = dao.count();
    List<Run> runs = new ArrayList<>();
    dao.saveAll(runs);
    assertSame(originalCount, dao.count());
  }

  @Test
  public void testSaveAllNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.saveAll(null);
  }

  private Run makeRun(String alias) {
    SecurityProfile profile = (SecurityProfile) sessionFactory.getCurrentSession().get(SecurityProfile.class, 3L);
    SequencerReference sequencer = emptySR;
    Date now = new Date();
    User user = new UserImpl();
    user.setUserId(1L);

    Run run = new Run();
    run.setSecurityProfile(profile);
    run.setAlias(alias);
    run.setDescription("description");
    run.setFilePath("/somewhere/someplace/");
    run.setSequencerReference(sequencer);
    run.setCreator(user);
    run.setCreationTime(now);
    run.setLastModifier(user);
    run.setLastModified(now);
    return run;
  }

  private void mockNonLazyThings() throws IOException {
    User mockUser = Mockito.mock(UserImpl.class);
    Mockito.when(securityDAO.getUserById(Matchers.anyLong())).thenReturn(mockUser);

    List<SequencerPartitionContainer> mockContainers = new ArrayList<>();
    mockContainers.add(Mockito.mock(SequencerPartitionContainerImpl.class));
    Mockito.when(sequencerPartitionContainerDAO.listAllSequencerPartitionContainersByRunId(Matchers.anyLong())).thenReturn(mockContainers);

    List<RunQC> mockQcs = new ArrayList<>();
    mockQcs.add(Mockito.mock(RunQCImpl.class));
    Mockito.when(runQcDAO.listByRunId(Matchers.anyLong())).thenReturn(mockQcs);
  }

  private void assertNonLazyThings(Run run) {
    assertNotNull(run);
    assertFalse(run.getSequencerPartitionContainers().isEmpty());
    assertFalse(run.getRunQCs().isEmpty());
  }

  @Test
  public void testListWithLimitAndOffset() throws IOException {
    List<Run> runs = dao.list(2, 2, true, "id");
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
    List<Run> runs = dao.list(2, 2, true, "id", PaginationFilter.query("C0"));
    assertEquals(1, runs.size());
    assertEquals(4L, runs.get(0).getId());
  }

  @Test
  public void testListByEmptySearchWithLimit() throws IOException {
    List<Run> runs = dao.list(0, 3, true, "id", PaginationFilter.query(""));
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
