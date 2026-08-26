package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListContainerView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateListContainerViewDaoIT extends AbstractDAOTest {

  private HibernateListContainerViewDao sut;

  @BeforeEach
  public void setup() {
    sut = new HibernateListContainerViewDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testListWithLimitAndOffset() throws IOException {
    List<ListContainerView> spcs = sut.list(1, 2, true, "id");
    assertEquals(2, spcs.size());
    assertEquals(2, spcs.get(0).getId());
  }

  @Test
  public void testCountBySearch() throws IOException {
    assertEquals(3, sut.count(PaginationFilter.query("C0*")));
  }

  @Test
  public void testCountByEmptySearch() throws IOException {
    assertEquals(4L, sut.count());
  }

  @Test
  public void testCountByBadSearch() throws IOException {
    assertEquals(0L, sut.count(PaginationFilter.query("; DROP TABLE SequencerPartitionContainer;")));
  }

  @Test
  public void testListBySearchWithLimit() throws IOException {
    List<ListContainerView> spcs = sut.list(2, 2, true, "id", PaginationFilter.query("C0*"));
    assertEquals(1, spcs.size());
    assertEquals(4L, spcs.get(0).getId());
  }

  @Test
  public void testListByEmptySearchWithLimit() throws IOException {
    List<ListContainerView> spcs = sut.list(0, 3, true, "id");
    assertEquals(3L, spcs.size());
  }

  @Test
  public void testListByBadSearchWithLimit() throws IOException {
    List<ListContainerView> spcs = sut.list(0, 2, true, "id",
        PaginationFilter.query("; DROP TABLE SequencerPartitionContainer;"));
    assertEquals(0L, spcs.size());
  }

  @Test
  public void testListOffsetBadLimit() throws IOException {
    assertThrows(IOException.class, () -> sut.list(5, -3, true, "id"));
  }

  @Test
  public void testListOffsetThreeWithThreeSamplesPerPageOrderLastMod() throws IOException {
    List<ListContainerView> spcs = sut.list(2, 2, false, "lastModified");
    assertEquals(2, spcs.size());
    assertEquals(2, spcs.get(0).getId());
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("Container"));
  }

  @Test
  public void testSearchByEntered() throws IOException {
    testSearch(
        PaginationFilter.date(LimsUtils.parseDate("2017-01-01"), LimsUtils.parseDate("2018-01-01"), DateType.ENTERED));
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

  @Test
  public void testSearchByIndex() throws IOException {
    testSearch(PaginationFilter.index("Index 01"));
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter) throws IOException {
    assertNotNull(sut.list(err -> {
      throw new RuntimeException(err);
    }, 0, 10, true, "identificationBarcode", filter));
  }

}
