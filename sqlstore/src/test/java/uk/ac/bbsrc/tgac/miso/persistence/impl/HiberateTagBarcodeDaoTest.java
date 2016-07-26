package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcodeFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class HiberateTagBarcodeDaoTest extends AbstractDAOTest {
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
    assertEquals("Index 8", tagBarcodeById.getName());
    assertEquals("TruSeq Single Index", tagBarcodeById.getFamily().getName());

  }

  @Test
  public void testListTagBarcodesByPlatform() throws Exception {
    Collection<TagBarcode> illumina = dao.listAllTagBarcodes(PlatformType.ILLUMINA);
    assertTrue(illumina.size() > 0);
  }

  @Test
  public void testListTagBarcodesByStrategyName() throws Exception {
    TagBarcodeFamily tagBarcodes = dao.getTagBarcodeFamilyByName("Nextera Dual Index");
    assertTrue(20 == tagBarcodes.getBarcodes().size());
  }
  
  @Test
  public void testListAllTagBarcodes() throws Exception {
    List<TagBarcode> list = dao.listAllTagBarcodes();
    assertEquals(80, list.size());
  }
  
  @Test
  public void testGetTagBarcodeFamilies() throws Exception {
    List<TagBarcodeFamily> list = dao.getTagBarcodeFamilies();
    assertEquals(12, list.size());
  }
  
  @Test
  public void testGetTagBarcodeFamiliesByPlatform() throws Exception {
    List<TagBarcodeFamily> list = dao.getTagBarcodeFamiliesByPlatform(PlatformType.ILLUMINA);
    assertEquals(11, list.size());
    int totalIlluminaBarcodes = 0;
    for (TagBarcodeFamily fam : list) {
      totalIlluminaBarcodes += fam.getBarcodes().size();
    }
    assertEquals(68, totalIlluminaBarcodes);
  }
}
