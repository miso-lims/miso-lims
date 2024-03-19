
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.persistence.IndexStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;

public class HibernateLibraryDaoIT extends AbstractDAOTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;
  @Mock
  private IndexStore indexStore;
  @Mock
  private SampleStore sampleStore;
  @Mock
  private ChangeLogStore changeLogDAO;

  @InjectMocks
  private HibernateLibraryDao dao;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
    dao.setDetailedSample(true);
  }

  @Test
  public void testCreate() throws Exception {

    Library library = new LibraryImpl();
    String libraryName = "newLibrary";
    library.setName(libraryName);
    library.setAlias("theAlias");
    library.setDescription("a description");
    library.setPlatformType(PlatformType.ILLUMINA);
    Sample sample = new SampleImpl();
    sample.setId(4);
    library.setSample(sample);
    LibraryType libraryType = new LibraryType();
    libraryType.setId(1L);
    library.setLibraryType(libraryType);
    LibrarySelectionType librarySelectionType = new LibrarySelectionType();
    librarySelectionType.setId(1L);
    library.setLibrarySelectionType(librarySelectionType);
    LibraryStrategyType libraryStrategyType = new LibraryStrategyType();
    libraryStrategyType.setId(1L);
    library.setLibraryStrategyType(libraryStrategyType);
    User mockUser = new UserImpl();
    mockUser.setId(1L);
    Date now = new Date();
    library.setCreator(mockUser);
    library.setCreationTime(now);
    library.setLastModifier(mockUser);
    library.setLastModified(now);

    long libraryId = dao.save(library);
    Library insertedLibrary = dao.get(libraryId);
    assertEquals(libraryName, insertedLibrary.getName());
    assertEquals("theAlias", insertedLibrary.getAlias());
    assertEquals("a description", insertedLibrary.getDescription());
    assertEquals(4, library.getSample().getId());
    assertEquals(1L, library.getLibraryType().getId());
    assertEquals(1L, library.getLibrarySelectionType().getId());
    assertEquals(1L, library.getLibraryStrategyType().getId());
  }

  @Test
  public void testUpdate() throws Exception {
    long id = 5L;
    String newDescription = "New Description";
    Library before = (Library) currentSession().get(LibraryImpl.class, id);
    assertNotEquals(newDescription, before.getDescription());
    before.setDescription(newDescription);
    dao.save(before);

    clearSession();

    Library after = (Library) currentSession().get(LibraryImpl.class, id);
    assertEquals(newDescription, after.getDescription());
  }

  @Test
  public void testGet() throws Exception {
    Library library = dao.get(3);
    assertNotNull(library);
    assertEquals("library name is incorrect", "LIB3", library.getName());
    assertEquals("library description is incorrect", "Inherited from TEST_0002", library.getDescription());
  }

  @Test
  public void testListByAlias() throws Exception {
    String alias = "TEST_0006_Bn_R_PE_300_WG";
    Collection<EntityReference> byAlias = dao.listByAlias(alias);
    assertNotNull(byAlias);
    assertEquals(1, byAlias.size());
    assertEquals("alias does not match", alias, byAlias.iterator().next().getLabel());
  }

  @Test
  public void testListBySampleId() throws Exception {
    long sampleId = 1L;
    List<Library> libraries = dao.listBySampleId(sampleId);

    assertEquals(2, libraries.size());
    assertEquals(sampleId, libraries.get(0).getSample().getId());
    assertEquals(sampleId, libraries.get(1).getSample().getId());
  }

  @Test
  public void testListByProjectId() throws Exception {
    List<Library> libraries = dao.listByProjectId(1);
    List<Long> libraryIds = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l);
    assertEquals(15, libraries.size());
    for (Library library : libraries) {
      assertTrue("bad library found", libraryIds.contains(library.getId()));
    }
  }

  @Test
  public void testListByIdList() throws Exception {
    List<Long> ids = Lists.newArrayList(1L, 3L, 5L);
    List<Library> libraries = dao.listByIdList(ids);
    assertNotNull(libraries);
    for (Long id : ids) {
      assertTrue(libraries.stream().anyMatch(x -> x.getId() == id.longValue()));
    }
  }

  @Test
  public void testListByIdListNull() throws Exception {
    List<Library> results = dao.listByIdList(null);
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }

  @Test
  public void testListByIdListNone() throws Exception {
    List<Library> results = dao.listByIdList(Collections.emptyList());
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }

  @Test
  public void testGetNextSibling() throws Exception {
    Library library = (Library) currentSession().get(LibraryImpl.class, 1L);
    long nextId = 2L;
    EntityReference next = dao.getAdjacentLibrary(library, false);
    assertNotNull(next);
    assertEquals(nextId, next.getId());
    Library nextLibrary = (Library) currentSession().get(LibraryImpl.class, nextId);
    assertEquals(library.getSample().getId(), nextLibrary.getSample().getId());
  }

  @Test
  public void testGetNextCousin() throws Exception {
    Library library = (Library) currentSession().get(LibraryImpl.class, 2L);
    long nextId = 3L;
    EntityReference next = dao.getAdjacentLibrary(library, false);
    assertNotNull(next);
    assertEquals(nextId, next.getId());
    Library nextLibrary = (Library) currentSession().get(LibraryImpl.class, nextId);
    assertNotEquals(library.getSample().getId(), nextLibrary.getSample().getId());
  }

  @Test
  public void testGetNextNull() throws Exception {
    Library library = (Library) currentSession().get(LibraryImpl.class, 15L);
    EntityReference next = dao.getAdjacentLibrary(library, false);
    assertNull(next);
  }

  @Test
  public void testGetPreviousSibling() throws Exception {
    Library library = (Library) currentSession().get(LibraryImpl.class, 2L);
    long previousId = 1L;
    EntityReference previous = dao.getAdjacentLibrary(library, true);
    assertNotNull(previous);
    assertEquals(previousId, previous.getId());
    Library previousLibrary = (Library) currentSession().get(LibraryImpl.class, previousId);
    assertEquals(library.getSample().getId(), previousLibrary.getSample().getId());
  }

  @Test
  public void testGetPreviousCousin() throws Exception {
    Library library = (Library) currentSession().get(LibraryImpl.class, 3L);
    long previousId = 2L;
    EntityReference previous = dao.getAdjacentLibrary(library, true);
    assertNotNull(previous);
    assertEquals(previousId, previous.getId());
    Library previousLibrary = (Library) currentSession().get(LibraryImpl.class, previousId);
    assertNotEquals(library.getSample().getId(), previousLibrary.getSample().getId());
  }

  public void testGetPreviousNull() throws Exception {
    Library library = (Library) currentSession().get(LibraryImpl.class, 1L);
    EntityReference previous = dao.getAdjacentLibrary(library, true);
    assertNull(previous);
  }

  @Test
  public void testListAll() throws Exception {
    List<Library> libraries = dao.listAll();
    List<Long> libraryIds = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l);
    assertEquals(15, libraries.size());
    for (Library library : libraries) {
      assertTrue("bad library found", libraryIds.contains(library.getId()));
    }

  }

  @Test
  public void testListWithLimitAndOffset() throws IOException {
    assertEquals(3, dao.list(5, 3, true, "id").size());
  }

  @Test
  public void testListBySearchWithLimit() throws IOException {
    List<Library> libraries = dao.list(2, 3, false, "lastModified", PaginationFilter.query("*Bn_R*"));
    assertEquals(3, libraries.size());
    assertEquals(10L, libraries.get(0).getId());
  }

  @Test
  public void testListByIlluminaBadSearchWithLimit() throws IOException {
    List<Library> libraries = dao.list(5, 3, true, "id", PaginationFilter.query("; DROP TABLE Library;"));
    assertEquals(0L, libraries.size());
  }

  @Test(expected = IOException.class)
  public void testListIlluminaOffsetBadLimit() throws IOException {
    dao.list(5, -3, true, "id");
  }

  @Test
  public void testListOffsetThreeWithThreeLibsPerPageOrderLastMod() throws IOException {
    List<Library> libraries = dao.list(3, 3, false, "lastModified");
    assertEquals(3, libraries.size());
    assertEquals(12, libraries.get(0).getId());
  }

  @Test
  public void testListIdsBySampleRequisitionIdPlain() throws Exception {
    List<Long> ids = dao.listIdsBySampleRequisitionId(1L);
    assertEquals(2, ids.size());
    assertTrue(ids.contains(1L));
    assertTrue(ids.contains(2L));
  }

  @Test
  public void testListIdsBySampleRequisitionIdDetailedWithSupplemental() throws Exception {
    Set<Long> aliquotSampleIds = Collections.singleton(19L);
    Mockito.when(sampleStore.getChildIds(Mockito.anyList(), Mockito.eq(SampleAliquot.CATEGORY_NAME), Mockito.eq(2L)))
        .thenReturn(aliquotSampleIds);

    List<Long> ids = dao.listIdsBySampleRequisitionId(2L);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
    Mockito.verify(sampleStore).getChildIds(captor.capture(), Mockito.eq(SampleAliquot.CATEGORY_NAME), Mockito.eq(2L));
    List<Long> requisitionSampleIdsUsed = captor.getValue();
    assertEquals(3, requisitionSampleIdsUsed.size());
    // requisitioned samples
    assertTrue(requisitionSampleIdsUsed.contains(16L));
    assertTrue(requisitionSampleIdsUsed.contains(17L));
    // supplemental sample
    assertTrue(requisitionSampleIdsUsed.contains(21L));

    assertEquals(1, ids.size());
    assertTrue(ids.contains(15L));
  }

  @Test
  public void testListIdsByAncestorSampleIdListDirect() throws Exception {
    List<Long> sampleIds = Arrays.asList(19L);
    Mockito.when(sampleStore.getChildIds(sampleIds, SampleAliquot.CATEGORY_NAME, null))
        .thenReturn(Collections.emptySet());

    List<Long> ids = dao.listIdsByAncestorSampleIdList(sampleIds, null);
    assertEquals(1, ids.size());
    assertTrue(ids.contains(15L));
  }

  @Test
  public void testListIdsByAncestorSampleIdListIndirect() throws Exception {
    List<Long> sampleIds = Arrays.asList(17L);
    Set<Long> aliquotSampleIds = Collections.singleton(19L);
    Mockito.when(sampleStore.getChildIds(sampleIds, SampleAliquot.CATEGORY_NAME, null))
        .thenReturn(aliquotSampleIds);

    List<Long> ids = dao.listIdsByAncestorSampleIdList(sampleIds, null);
    assertEquals(1, ids.size());
    assertTrue(ids.contains(15L));
  }

}
