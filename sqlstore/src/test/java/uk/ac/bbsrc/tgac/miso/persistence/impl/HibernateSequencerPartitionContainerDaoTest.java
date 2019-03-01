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

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateSequencerPartitionContainerDaoTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private SecurityStore securityDao;

  @InjectMocks
  private HibernateSequencerPartitionContainerDao dao;

  private final User emptyUser = new UserImpl();

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);

    emptyUser.setUserId(1L);
    when(securityDao.getUserById(Matchers.anyLong())).thenReturn(emptyUser);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<SequencerPartitionContainer> spcs = dao.listAll();
    assertEquals(4, spcs.size());
  }

  @Test
  public void testPCCount() throws IOException {
    assertEquals(4, dao.count());
  }

  @Test
  public void testListByBarcodeC075RACXX() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listSequencerPartitionContainersByBarcode("C075RACXX");
    assertEquals(1, spcs.size());
  }

  @Test
  public void testListByBarcodeNone() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listSequencerPartitionContainersByBarcode("A0AAAAAXX");
    assertEquals(0, spcs.size());
  }

  @Test
  public void testListByBarcodeEmpty() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listSequencerPartitionContainersByBarcode("A0AAAAAXX");
    assertEquals(0, spcs.size());
  }

  @Test
  public void testListByRunId() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listAllSequencerPartitionContainersByRunId(1L);
    assertEquals(1, spcs.size());
  }

  @Test
  public void testListByRunIdNone() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listAllSequencerPartitionContainersByRunId(9999L);
    assertEquals(0, spcs.size());
  }

  @Test
  public void testGetByPartitionId() throws IOException {
    SequencerPartitionContainer spc = dao.getSequencerPartitionContainerByPartitionId(1L);
    assertNotNull(spc);
  }

  @Test
  public void testGetByPartitionIdNone() throws IOException {
    SequencerPartitionContainer spc = dao.getSequencerPartitionContainerByPartitionId(9999L);
    assertNull(spc);
  }

  @Test
  public void testGet() throws IOException {
    SequencerPartitionContainer spc = dao.get(1L);
    assertNonLazyThings(spc);
  }

  @Test
  public void testSaveEdit() throws IOException {
    SequencerPartitionContainer spc = dao.get(4L);

    SequencingContainerModel model = (SequencingContainerModel) sessionFactory.getCurrentSession().get(SequencingContainerModel.class, 1L);
    spc.setModel(model);
    spc.setLastModifier(emptyUser);
    Run run = Mockito.mock(Run.class);
    Mockito.when(run.getId()).thenReturn(1L);
    spc.setIdentificationBarcode("ABCDEFXX");

    dao.save(spc);
    assertEquals(4L, spc.getId());
    SequencerPartitionContainer savedSPC = dao.get(4L);
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
    SequencerPartitionContainer newSPC = makeSPC("ABCDEFXX");

    assertEquals(SequencerPartitionContainerImpl.UNSAVED_ID, newSPC.getId());
    dao.save(newSPC);
    assertNotEquals(SequencerPartitionContainerImpl.UNSAVED_ID, newSPC.getId());

    SequencerPartitionContainer savedSPC = dao.get(newSPC.getId());
    assertEquals(newSPC.getIdentificationBarcode(), savedSPC.getIdentificationBarcode());
  }

  private SequencerPartitionContainer makeSPC(String identificationBarcode) throws IOException {
    SequencerPartitionContainer pc = new SequencerPartitionContainerImpl();
    Date now = new Date();
    pc.setIdentificationBarcode(identificationBarcode);
    SequencingContainerModel model = (SequencingContainerModel) sessionFactory.getCurrentSession().get(SequencingContainerModel.class, 1L);
    pc.setModel(model);
    pc.setCreationTime(now);
    pc.setCreator(emptyUser);
    pc.setLastModified(now);
    pc.setLastModifier(emptyUser);
    return pc;
  }

  private void assertNonLazyThings(SequencerPartitionContainer spc) {
    assertNotNull(spc);
    assertFalse(spc.getPartitions().isEmpty());
  }

  @Test
  public void testListWithLimitAndOffset() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.list(1, 2, true, "id");
    assertEquals(2, spcs.size());
    assertEquals(2, spcs.get(0).getId());
  }

  @Test
  public void testCountBySearch() throws IOException {
    assertEquals(3, dao.count(PaginationFilter.query("C0")));
  }

  @Test
  public void testCountByEmptySearch() throws IOException {
    assertEquals(4L, dao.count(PaginationFilter.query("")));
  }

  @Test
  public void testCountByBadSearch() throws IOException {
    assertEquals(0L, dao.count(PaginationFilter.query("; DROP TABLE SequencerPartitionContainer;")));
  }

  @Test
  public void testListBySearchWithLimit() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.list(2, 2, true, "id", PaginationFilter.query("C0"));
    assertEquals(1, spcs.size());
    assertEquals(4L, spcs.get(0).getId());
  }

  @Test
  public void testListByEmptySearchWithLimit() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.list(0, 3, true, "id", PaginationFilter.query(""));
    assertEquals(3L, spcs.size());
  }

  @Test
  public void testListByBadSearchWithLimit() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.list(0, 2, true, "id",
        PaginationFilter.query("; DROP TABLE SequencerPartitionContainer;"));
    assertEquals(0L, spcs.size());
  }

  @Test
  public void testListOffsetBadLimit() throws IOException {
    exception.expect(IOException.class);
    dao.list(5, -3, true, "id");
  }

  @Test
  public void testListOffsetThreeWithThreeSamplesPerPageOrderLastMod() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.list(2, 2, false, "lastModified");
    assertEquals(2, spcs.size());
    assertEquals(2, spcs.get(0).getId());
  }

  @Test
  public void testGetModel() throws Exception {
    assertNotNull(dao.get(1L));
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("Container"));
  }

  @Test
  public void testSearchByCreated() throws IOException {
    testSearch(PaginationFilter.date(LimsUtils.parseDate("2017-01-01"), LimsUtils.parseDate("2018-01-01"), DateType.CREATE));
  }

  @Test
  public void testSearchByCreator() throws IOException {
    testSearch(PaginationFilter.user("admin", true));
  }

  @Test
  public void testSearchByModifier() throws IOException {
    testSearch(PaginationFilter.user("admin", false));
  }

  @Test
  public void testSearchByPlatform() throws IOException {
    testSearch(PaginationFilter.platformType(PlatformType.ILLUMINA));
  }

  @Test
  public void testSearchByKitName() throws IOException {
    testSearch(PaginationFilter.kitName("Test Kit"));
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter) throws IOException {
    // verify Hibernate mappings by ensuring that no exception is thrown
    assertNotNull(dao.list(err -> {
      throw new RuntimeException(err);
    }, 0, 10, true, "identificationBarcode", filter));
  }
}
