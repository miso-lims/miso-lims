package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcodeFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class HiberateTagBarcodeStrategyDaoTest extends AbstractDAOTest {
  private HibernateTagBarcodeDao dao;
  @Autowired
  private SessionFactory sessionFactory;

  @Before
  public void setup() {
    dao = new HibernateTagBarcodeDao();
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGetTagBarcodeById() throws Exception {
    TagBarcode tagBarcodeById = dao.getTagBarcodeById(8);
    assertEquals(8L, tagBarcodeById.getId());
    assertEquals("ACTTGA", tagBarcodeById.getSequence());
    assertEquals("Index 08", tagBarcodeById.getName());
    assertEquals("TruSeq Single Index", tagBarcodeById.getFamily().getName());

  }

  @Test
  public void testListTagBarcodesByPlatform() throws Exception {
    Collection<TagBarcode> illumina = dao.listAllTagBarcodes(PlatformType.ILLUMINA);
    assertTrue(68 == illumina.size());
  }

  @Test
  public void testListTagBarcodesByStrategyName() throws Exception {
    TagBarcodeFamily tagBarcodes = dao.getTagBarcodeFamilyByName("Nextera Dual Index");
    assertTrue(20 == tagBarcodes.getBarcodes().size());
  }
}
