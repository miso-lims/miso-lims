package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;

public class HibernateBarcodableViewDaoIT extends AbstractDAOTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  private HibernateBarcodableViewDao dao;

  @PersistenceContext
  private EntityManager entityManager;

  @Before
  public void setup() {
    dao = new HibernateBarcodableViewDao();
    dao.setEntityManager(entityManager);
  }

  @Test
  public void testSearchNull() {
    exception.expect(IllegalArgumentException.class);
    dao.search(null);
  }

  @Test
  public void testSearchLibraryAliquot() {
    testSearch("LDI1::TEST_0001_Bn_P_PE_300_WG", EntityType.LIBRARY_ALIQUOT, 1);
  }

  @Test
  public void testSearchPool() {
    testSearch("IPO2::Illumina", EntityType.POOL, 2);
  }

  @Test
  public void testSearchSample() {
    testSearch("SAM3::TEST_0002_Bn_P_nn_1-1_D_1", EntityType.SAMPLE, 3);
  }

  @Test
  public void testSearchBox() {
    testSearch("identificationbarcode1", EntityType.BOX, 1);
  }

  @Test
  public void testSearchLibrary() {
    testSearch("LIB4::TEST_0002_Bn_R_PE_300_WG", EntityType.LIBRARY, 4);
  }

  @Test
  public void testSearchContainer() {
    testSearch("D0VJ9ACXX", EntityType.CONTAINER, 2);
  }

  @Test
  public void testSearchByBarcodeUnknown() {
    assertTrue(dao.searchByBarcode("not a barcode", Collections.singletonList(EntityType.LIBRARY_ALIQUOT)).isEmpty());
  }

  @Test
  public void testSearchByBarcode() {
    testSearchByBarcode("LDI1::TEST_0001_Bn_P_PE_300_WG", Collections.singletonList(EntityType.LIBRARY_ALIQUOT),
        EntityType.LIBRARY_ALIQUOT, 1);
  }

  @Test
  public void testSearchByBarcodeNull() {
    exception.expect(IllegalArgumentException.class);
    dao.searchByBarcode(null, Arrays.asList(EntityType.SAMPLE));
  }

  @Test
  public void testSearchByBarcodeNullTypes() {
    exception.expect(IllegalArgumentException.class);
    dao.searchByBarcode("asdf", null);
  }

  @Test
  public void testSearchByBarcodeNoTypes() {
    exception.expect(IllegalArgumentException.class);
    dao.searchByBarcode("asdf", Collections.emptyList());
  }

  @Test
  public void testSearchByBarcodeMultipleEntityTypes() {
    testSearchByBarcode("LDI1::TEST_0001_Bn_P_PE_300_WG", Arrays.asList(EntityType.BOX, EntityType.LIBRARY_ALIQUOT),
        EntityType.LIBRARY_ALIQUOT, 1);
  }

  @Test
  public void testSearchByBarcodeIncorrectEntityType() {
    assertTrue(
        dao.searchByBarcode("LDI1::TEST_0001_Bn_P_PE_300_WG", Collections.singletonList(EntityType.BOX)).isEmpty());
  }

  @Test
  public void testSearchByBarcodeIncorrectEntityTypes() {
    assertTrue(
        dao.searchByBarcode("LDI1::TEST_0001_Bn_P_PE_300_WG", Arrays.asList(EntityType.BOX, EntityType.CONTAINER))
            .isEmpty());
  }

  @Test
  public void testSearchByAliasNull() {
    exception.expect(IllegalArgumentException.class);
    dao.searchByAlias(null, Arrays.asList(EntityType.SAMPLE));
  }

  @Test
  public void testSearchByAliasNullTypes() {
    exception.expect(IllegalArgumentException.class);
    dao.searchByAlias("asdf", null);
  }

  @Test
  public void testSearchByAliasNoTypes() {
    exception.expect(IllegalArgumentException.class);
    dao.searchByAlias("asdf", Collections.emptyList());
  }

  @Test
  public void testSearchByAliasSingleType() {
    testSearchByAlias("TEST_0001_TISSUE_2", Arrays.asList(EntityType.SAMPLE), EntityType.SAMPLE, 17);
  }

  @Test
  public void testSearchByAliasMultipleType() {
    testSearchByAlias("TEST_0001_TISSUE_2", Arrays.asList(EntityType.BOX, EntityType.SAMPLE, EntityType.LIBRARY),
        EntityType.SAMPLE, 17);
  }

  @Test
  public void testCheckForExistingSample() throws Exception {
    testCheckForExisting("SAM19::TEST_0001_ALIQUOT_1", "TEST_0001_ALIQUOT_1");
  }

  @Test
  public void testCheckForExistingLibrary() throws Exception {
    testCheckForExisting("LIB4::TEST_0002_Bn_R_PE_300_WG", "TEST_0002_Bn_R_PE_300_WG");
  }

  @Test
  public void testCheckForExistingLibraryAliquot() throws Exception {
    testCheckForExisting("LDI7::TEST_0004_Bn_P_PE_300_WG", "TEST_0004_Bn_P_PE_300_WG");
  }

  @Test
  public void testCheckForExistingPool() throws Exception {
    testCheckForExisting("IPO3::Illumina", "Pool 3");
  }

  @Test
  public void testCheckForExistingBox() throws Exception {
    testCheckForExisting("identificationbarcode2", "box2alias");
  }

  @Test
  public void testCheckForExistingContainer() throws Exception {
    testCheckForExisting("C0JHTACXX", "C0JHTACXX");
  }

  @Test
  public void testCheckForExistingContainerModel() throws Exception {
    testCheckForExisting("12345678", "HiSeq PE Flow Cell v4");
  }

  @Test
  public void testCheckForExistingNull() throws Exception {
    exception.expect(IllegalArgumentException.class);
    assertNull(dao.checkForExisting(null));
  }

  @Test
  public void testCheckForExistingEmpty() throws Exception {
    exception.expect(IllegalArgumentException.class);
    assertNull(dao.checkForExisting(""));
  }

  @Test
  public void testCheckForExistingNone() throws Exception {
    assertNull(dao.checkForExisting("notabarcode"));
  }

  private void testSearchByBarcode(String identificationBarcode, Collection<EntityType> typeFilter,
      EntityType targetType,
      long targetId) {
    List<BarcodableView> results = dao.searchByBarcode(identificationBarcode, typeFilter);
    assertSingleResult(results, targetType, targetId);
  }

  private void testSearchByAlias(String alias, Collection<EntityType> typeFilter, EntityType targetType,
      long targetId) {
    List<BarcodableView> results = dao.searchByAlias(alias, typeFilter);
    assertSingleResult(results, targetType, targetId);
  }

  private void testSearch(String identificationBarcode, EntityType targetType, long targetId) {
    List<BarcodableView> results = dao.search(identificationBarcode);
    assertSingleResult(results, targetType, targetId);
  }

  private void testCheckForExisting(String barcode, String primaryLabel) throws IOException {
    BarcodableReference reference = dao.checkForExisting(barcode);
    assertNotNull(reference);
    assertEquals(primaryLabel, reference.getPrimaryLabel());
  }

  private void assertSingleResult(List<BarcodableView> results, EntityType targetType, long targetId) {
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals(targetType, results.get(0).getId().getTargetType());
    assertEquals(targetId, results.get(0).getId().getTargetId());
  }
}
