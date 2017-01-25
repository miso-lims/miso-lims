/* Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * * *********************************************************************
 * *
 * * This file is part of MISO.
 * *
 * * MISO is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * MISO is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 * *
 * * *********************************************************************
 * */

package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateChangeLogDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencerPartitionContainerDao;

public class SQLSequencerPartitionContainerDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private HibernateChangeLogDao changeLogDAO;

  @InjectMocks
  private HibernateSequencerPartitionContainerDao dao;

  // Auto-increment sequence doesn't roll back with transactions, so must be tracked
  private static long nextAutoIncrementId = 5L;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listAll();
    assertEquals(4, spcs.size());
  }

  @Test
  public void testPCCount() throws IOException {
    assertEquals(4, dao.count());
  }

  @Test
  public void testListByBarcodeC075RACXX() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listSequencerPartitionContainersByBarcode("C075RACXX");
    assertEquals(1, spcs.size());
  }

  @Test
  public void testListByBarcodeNone() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listSequencerPartitionContainersByBarcode("A0AAAAAXX");
    assertEquals(0, spcs.size());
  }

  @Test
  public void testListByBarcodeEmpty() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listSequencerPartitionContainersByBarcode("A0AAAAAXX");
    assertEquals(0, spcs.size());
  }

  @Test
  public void testListByRunId() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listAllSequencerPartitionContainersByRunId(1L);
    assertEquals(1, spcs.size());
  }

  @Test
  public void testListByRunIdNone() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listAllSequencerPartitionContainersByRunId(9999L);
    assertEquals(0, spcs.size());
  }

  @Test
  public void testGetByPartitionId() throws IOException {
    SequencerPartitionContainer<SequencerPoolPartition> spc = dao.getSequencerPartitionContainerByPartitionId(1L);
    assertNotNull(spc);
  }

  @Test
  public void testGetByPartitionIdNone() throws IOException {
    SequencerPartitionContainer<SequencerPoolPartition> spc = dao.getSequencerPartitionContainerByPartitionId(9999L);
    assertNull(spc);
  }

  @Test
  public void testGet() throws IOException {
    SequencerPartitionContainer<SequencerPoolPartition> spc = dao.get(1L);
    assertNonLazyThings(spc);
  }

  @Test
  public void testSaveEdit() throws IOException {
    SequencerPartitionContainer<SequencerPoolPartition> spc = dao.get(4L);

    SecurityProfile profile = Mockito.mock(SecurityProfile.class);
    spc.setSecurityProfile(profile);
    Mockito.when(profile.getProfileId()).thenReturn(1L);
    Platform platform = Mockito.mock(PlatformImpl.class);
    spc.setPlatform(platform);
    Mockito.when(platform.getId()).thenReturn(1L);
    User user = Mockito.mock(UserImpl.class);
    Mockito.when(user.getUserId()).thenReturn(1L);
    spc.setLastModifier(user);
    Run run = Mockito.mock(RunImpl.class);
    Mockito.when(run.getId()).thenReturn(1L);
    spc.setIdentificationBarcode("ABCDEFXX");
    spc.setRun(run);

    assertEquals(4L, dao.save(spc));
    SequencerPartitionContainer<SequencerPoolPartition> savedSPC = dao.get(4L);
    assertNotSame(spc, savedSPC);
    assertEquals(spc.getId(), savedSPC.getId());
    assertEquals("ABCDEFXX", savedSPC.getIdentificationBarcode());
  }

  @Test
  public void testSaveNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.save(null);
  }

  @Test
  public void testSaveNew() throws IOException {
    long autoIncrementId = nextAutoIncrementId;
    SequencerPartitionContainer<SequencerPoolPartition> newSPC = makeSPC("ABCDEFXX");

    assertEquals(autoIncrementId, dao.save(newSPC));

    SequencerPartitionContainer<SequencerPoolPartition> savedSPC = dao.get(autoIncrementId);
    assertEquals(newSPC.getIdentificationBarcode(), savedSPC.getIdentificationBarcode());
    nextAutoIncrementId += 1;
  }

  @Test
  public void testRemove() throws IOException {
    SequencerPartitionContainer<SequencerPoolPartition> spc = new SequencerPartitionContainerImpl();
    String spcIDBC = "ABCDEFXX";
    spc.setIdentificationBarcode(spcIDBC);
    spc.setPlatform(Mockito.mock(PlatformImpl.class));
    User mockUser = Mockito.mock(UserImpl.class);
    when(mockUser.getUserId()).thenReturn(1L);
    spc.setLastModifier(mockUser);

    long spcId = dao.save(spc);
    SequencerPartitionContainer<SequencerPoolPartition> insertedSpc = dao.get(spcId);
    assertNotNull(insertedSpc);
    assertTrue(dao.remove(spc));
    Mockito.verify(changeLogDAO, Mockito.times(1)).deleteAllById("SequencerPartitionContainer", spc.getId());
    assertNull(dao.get(insertedSpc.getId()));
    nextAutoIncrementId++;
  }

  @Test
  public void testRemoveContainerFromRun() throws IOException {
    assertEquals(1, dao.listAllSequencerPartitionContainersByRunId(1L).size());

    SequencerPartitionContainer<SequencerPoolPartition> spc = dao.get(1L);
    assertEquals(0, dao.listAllSequencerPartitionContainersByRunId(1L).size());
  }

  @Test
  public void testRemoveContainerFromUnassociatedRun() throws IOException {
    assertEquals(1, dao.listAllSequencerPartitionContainersByRunId(1L).size());

    SequencerPartitionContainer<SequencerPoolPartition> spc = dao.get(2L);
    assertEquals(1, dao.listAllSequencerPartitionContainersByRunId(1L).size());
  }

  private SequencerPartitionContainer<SequencerPoolPartition> makeSPC(String identificationBarcode) throws IOException {
    SecurityProfile profile = Mockito.mock(SecurityProfile.class);
    User user = Mockito.mock(UserImpl.class);
    Mockito.when(user.getUserId()).thenReturn(1L);
    SequencerPartitionContainer<SequencerPoolPartition> pc = new SequencerPartitionContainerImpl();
    pc.setSecurityProfile(profile);
    pc.setIdentificationBarcode(identificationBarcode);
    pc.setLocationBarcode("location");
    Platform platform = new PlatformImpl();
    platform.setId(1L);
    pc.setPlatform(platform);
    pc.setLastModifier(user);
    return pc;
  }

  private void assertNonLazyThings(SequencerPartitionContainer<SequencerPoolPartition> spc) {
    assertNotNull(spc);
    assertFalse(spc.getPartitions().isEmpty());
  }

  @Test
  public void testListWithLimitAndOffset() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listByOffsetAndNumResults(1, 2, "asc", "id");
    assertEquals(2, spcs.size());
    assertEquals(2, spcs.get(0).getId());
  }

  @Test
  public void testCountBySearch() throws IOException {
    assertEquals(3, dao.countBySearch("C0"));
  }

  @Test
  public void testCountByEmptySearch() throws IOException {
    assertEquals(4L, dao.countBySearch(""));
  }

  @Test
  public void testCountByBadSearch() throws IOException {
    assertEquals(0L, dao.countBySearch("; DROP TABLE SequencerPartitionContainer;"));
  }

  @Test
  public void testListBySearchWithLimit() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listBySearchOffsetAndNumResults(2, 2, "C0", "asc", "id");
    assertEquals(1, spcs.size());
    assertEquals(4L, spcs.get(0).getId());
  }

  @Test
  public void testListByEmptySearchWithLimit() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listBySearchOffsetAndNumResults(0, 3, "", "asc", "id");
    assertEquals(3L, spcs.size());
  }

  @Test
  public void testListByBadSearchWithLimit() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao
        .listBySearchOffsetAndNumResults(0, 2, "; DROP TABLE SequencerPartitionContainer;", "asc", "id");
    assertEquals(0L, spcs.size());
  }

  @Test
  public void testListByOffsetBadSortDir() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listByOffsetAndNumResults(1, 3, "BARK", "id");
    assertEquals(3, spcs.size());
  }

  @Test
  public void testListOffsetBadLimit() throws IOException {
    exception.expect(IOException.class);
    dao.listByOffsetAndNumResults(5, -3, "asc", "id");
  }

  @Test
  public void testListOffsetThreeWithThreeSamplesPerPageOrderLastMod() throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> spcs = dao.listByOffsetAndNumResults(2, 2, "desc", "lastModified");
    assertEquals(2, spcs.size());
    assertEquals(2, spcs.get(0).getId());
  }
}
