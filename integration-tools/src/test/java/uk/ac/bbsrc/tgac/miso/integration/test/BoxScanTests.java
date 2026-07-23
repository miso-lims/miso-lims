package uk.ac.bbsrc.tgac.miso.integration.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;

public abstract class BoxScanTests<T extends BoxScan> {
  
  /**
   * Returns a new BoxScan, representing a scan of a full box of tubes. Implementations must return a BoxScan implementation object 
   * containing 2 rows, 2 columns, and the following barcodes:
   * <OL>
   *  <LI>A01: 11111</LI>
   *  <LI>B01: 22222</LI>
   *  <LI>A02: 33333</LI>
   *  <LI>B02: 44444</LI>
   * </OL>
   */
  protected abstract T getFullScan();
  
  /**
   * Returns a new BoxScan, representing a scan of an empty box of tubes. Implementations must return a BoxScan implementation object 
   * containing 2 rows, 2 columns, and no tubes. 
   */
  protected abstract T getEmptyScan();
  
  /**
   * Returns a new BoxScan, representing a scan of a full box of tubes with failed reads. Implementations must return a BoxScan 
   * implementation object containing 2 rows, 2 columns, and the following barcodes:
   * <OL>
   *  <LI>A01: 11111</LI>
   *  <LI>B01: (failed read)</LI>
   *  <LI>A02: (failed read)</LI>
   *  <LI>B02: 44444</LI>
   * </OL>
   */
  protected abstract T getErredScan();

  @Test
  public void testReferenceConversions() {
    BoxScan fullScan = getFullScan();
    assertEquals("33333",fullScan.getBarcode("A02"));
    assertEquals("33333",fullScan.getBarcode(1,2));
    assertEquals("33333",fullScan.getBarcode('A',2));
  }

  @Test
  public void testImmutability() {
    BoxScan fullScan = getFullScan();
    Map<String, String> map = fullScan.getBarcodesMap();
    String position = BoxUtils.getPositionString(0, 0);
    assertEquals("11111", map.get(position));
    assertEquals("A01", position);
    try {
      map.put(position, "changed");
    } catch (UnsupportedOperationException e) {
      // ignore exception. This means the map itself is immutable
    }
    map = fullScan.getBarcodesMap();
    assertEquals("11111", map.get(position));
    assertEquals(map.get("A01"), map.get(position));
  }
  
  @Test
  public void testFullScan() {
    BoxScan fullScan = getFullScan();
    assertTrue(fullScan.isFull());
    assertFalse(fullScan.isEmpty());
    assertEquals(2, fullScan.getColumnCount());
    assertEquals(2, fullScan.getRowCount());
    assertEquals(4, fullScan.getMaximumTubeCount());
    assertEquals(4, fullScan.getTubeCount());
    assertFalse(fullScan.hasReadErrors());
  }
  
  @Test
  public void testEmptyScan() {
    BoxScan emptyScan = getEmptyScan();
    assertFalse(emptyScan.isFull());
    assertTrue(emptyScan.isEmpty());
    assertEquals(2, emptyScan.getColumnCount());
    assertEquals(2, emptyScan.getRowCount());
    assertEquals(4, emptyScan.getMaximumTubeCount());
    assertEquals(0, emptyScan.getTubeCount());
    assertFalse(emptyScan.hasReadErrors());
  }
  
  @Test
  public void testFullScanWithReadError() {
    BoxScan erredScan = getErredScan();
    assertTrue(erredScan.isFull());
    assertFalse(erredScan.isEmpty());
    assertEquals(2, erredScan.getColumnCount());
    assertEquals(2, erredScan.getRowCount());
    assertEquals(4, erredScan.getMaximumTubeCount());
    assertEquals(4, erredScan.getTubeCount());
    assertTrue(erredScan.hasReadErrors());
    List<String> errPositions = erredScan.getReadErrorPositions();
    assertEquals(2, errPositions.size());
    assertTrue("A02".equals(errPositions.get(0)) || "A02".equals(errPositions.get(1)));
    assertTrue("B01".equals(errPositions.get(0)) || "B01".equals(errPositions.get(1)));
  }
}