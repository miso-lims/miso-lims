package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcNodeType;

public class HibernateQcNodeDaoIT extends AbstractDAOTest {

  private HibernateQcNodeDao sut;

  @Before
  public void setup() {
    sut = new HibernateQcNodeDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGetForSample() throws Exception {
    // Test hierarchy: Identity SAM15 < Tissue SAM17 > Slide SAM25 > Stock SAM18 > Aliquot SAM19 > LIB15
    // > LDI15

    QcNode root = sut.getForSample(17L);
    assertNotNull(root);
    assertEquals("SAM15", root.getName());
    List<? extends QcNode> children = root.getChildren();
    assertEquals(1, children.size());
    assertEquals("SAM17", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(1, children.size());
    assertEquals("SAM25", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(1, children.size());
    assertEquals("SAM18", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(3, children.size());
    assertEquals("SAM19", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(1, children.size());
    assertEquals("LIB15", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(1, children.size());
    assertEquals("LDI15", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(0, children.size());
  }

  @Test
  public void testGetForLibrary() throws Exception {
    testSam1Hierarchy(sut.getForLibrary(1L), true);
  }

  @Test
  public void testGetForLibraryAliquot() throws Exception {
    testSam1Hierarchy(sut.getForLibraryAliquot(1L), true);
  }

  @Test
  public void testGetForRunLibrary() throws Exception {
    testSam1Hierarchy(sut.getForRunLibrary(1L, 1L, 1L), false);
  }

  private void testSam1Hierarchy(QcNode root, boolean fromAbove) {
    // Test hierarchy: Plain SAM1 < LIB1 < LDI1
    // > IPO1 > RUN1 > Partition 1 > RunLibrary
    // > IPO4 (only visible if requested node is above in hierarchy)
    assertNotNull(root);
    assertEquals("SAM1", root.getName());
    List<? extends QcNode> children = root.getChildren();
    assertEquals(1, children.size());
    assertEquals("LIB1", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(1, children.size());
    assertEquals("LDI1", children.get(0).getName());
    children = children.get(0).getChildren();
    if (fromAbove) {
      assertEquals(2, children.size());
      QcNode pool4 = children.stream().filter(node -> "IPO4".equals(node.getName())).findAny().orElse(null);
      assertNotNull(pool4);
      assertEquals(0, pool4.getChildren().size());
    } else {
      assertEquals(1, children.size());
    }
    QcNode pool1 = children.stream().filter(node -> "IPO1".equals(node.getName())).findAny().orElse(null);
    assertNotNull(pool1);
    children = pool1.getChildren();
    assertEquals(1, children.size());
    assertEquals("RUN1", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(1, children.size());
    assertEquals(QcNodeType.RUN_PARTITION, children.get(0).getEntityType());
    children = children.get(0).getChildren();
    assertEquals(1, children.size());
    assertEquals(QcNodeType.RUN_LIBRARY, children.get(0).getEntityType());
  }

}
