package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;

public class HibernateBarcodableViewDaoTest extends AbstractDAOTest {
  @InjectMocks
  private HibernateBarcodableViewDao dao;

  @Autowired
  private SessionFactory sessionFactory;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testSearchForUnknownBarcode() {
    assertTrue(dao.searchByBarcode("not a barcode").isEmpty());
  }

  @Test
  public void testSearchDilution() {
    searchForBarcodeReturnsId("LDI1::TEST_0001_Bn_P_PE_300_WG", 1);
  }

  @Test
  public void testSearchPool() {
    searchForBarcodeReturnsId("IPO2::Illumina", 2);
  }

  @Test
  public void testSearchSample() {
    searchForBarcodeReturnsId("SAM3::TEST_0002_Bn_P_nn_1-1_D_1", 3);
  }

  @Test
  public void testSearchBox() {
    searchForBarcodeReturnsId("identificationbarcode1", 1);
  }

  @Test
  public void testSearchLibrary() {
    searchForBarcodeReturnsId("LIB4::TEST_0002_Bn_R_PE_300_WG", 4);
  }

  @Test
  public void testSearchContainer() {
    searchForBarcodeReturnsId("D0VJ9ACXX", 2);
  }

  @Test
  public void testSearchByUnknownBarcodeAndEntityType() {
    assertTrue(dao.searchByBarcode("not a barcode", Collections.singletonList(EntityType.DILUTION)).isEmpty());
  }

  @Test
  public void testSearchForIncorrectEntityType() {
    assertTrue(dao.searchByBarcode("LDI1::TEST_0001_Bn_P_PE_300_WG", Collections.singletonList(EntityType.BOX)).isEmpty());
  }

  @Test
  public void testSearchForMultipleIncorrectEntityTypes() {
    assertTrue(dao.searchByBarcode("LDI1::TEST_0001_Bn_P_PE_300_WG", Arrays.asList(EntityType.BOX, EntityType.CONTAINER)).isEmpty());
  }

  @Test
  public void testSearchForCorrectEntityType() {
    searchForBarcodeAndEntityReturnsId("LDI1::TEST_0001_Bn_P_PE_300_WG", Collections.singletonList(EntityType.DILUTION), 1);
  }

  @Test
  public void testSearchForMultipleEntityTypes() {
    searchForBarcodeAndEntityReturnsId("LDI1::TEST_0001_Bn_P_PE_300_WG", Arrays.asList(EntityType.BOX, EntityType.DILUTION), 1);
  }

  private void searchForBarcodeAndEntityReturnsId(String identificationBarcode, Collection<EntityType> typeFilter, long targetId) {
    assertEquals(dao.searchByBarcode(identificationBarcode, typeFilter).get(0).getId().getTargetId(), targetId);
  }

  private void searchForBarcodeReturnsId(String identificationBarcode, long targetId) {
    assertEquals(dao.searchByBarcode(identificationBarcode).get(0).getId().getTargetId(), targetId);
  }
}