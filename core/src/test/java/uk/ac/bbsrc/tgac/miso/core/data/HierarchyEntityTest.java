package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;

public class HierarchyEntityTest {

  @Test
  public void testGetFailingParentNone() {
    testGetFailingParentNode(null, true, true, true);
    testGetFailingParentNode(null, null, null, null);
    testGetFailingParentNode(null, false, true, true);
    testGetFailingParentNode(null, false, null, null);
    testGetFailingParentNode("1", null, false, null);
    testGetFailingParentNode("1", true, false, true);
    testGetFailingParentNode("1", false, false, true);
    testGetFailingParentNode("2", null, null, false);
    testGetFailingParentNode("2", true, true, false);
    testGetFailingParentNode("2", false, false, false);
  }

  private static void testGetFailingParentNode(String expectedAlias, Boolean... qcPassedNodes) {
    HierarchyEntity sut = makeHierarchy(qcPassedNodes);
    if (expectedAlias == null) {
      assertNull(sut.getFailingParent());
    } else {
      HierarchyEntity result = sut.getFailingParent();
      assertNotNull(result);
      assertEquals(expectedAlias, result.getAlias());
    }
  }

  private static HierarchyEntity makeHierarchy(Boolean... qcPassedNodes) {
    DetailedSample current = null;
    for (int i = qcPassedNodes.length - 1; i >= 0; i--) {
      current = makeItem(current, Integer.toString(i), qcPassedNodes[i]);
    }
    return current;
  }

  private static DetailedSample makeItem(DetailedSample parent, String alias, Boolean qcPassed) {
    DetailedSample item = new DetailedSampleImpl();
    item.setAlias(alias);
    DetailedQcStatus status = new DetailedQcStatusImpl();
    status.setStatus(qcPassed);
    item.setDetailedQcStatus(status);
    item.setParent(parent);
    return item;
  }

}
