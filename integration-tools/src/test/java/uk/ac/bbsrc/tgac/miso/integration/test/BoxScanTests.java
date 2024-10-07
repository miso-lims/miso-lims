package uk.ac.bbsrc.tgac.miso.integration.test;

import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

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
  public void testCollectionComparison() {
    BoxScan fullScan = getFullScan();
    String[][] array = fullScan.getBarcodesArray();
    assertEquals(array[0][0], "11111");
    assertEquals(array[1][0], "22222");
    assertEquals(array[0][1], "33333");
    assertEquals(array[1][1], "44444");
    
    Map<String, String> map = fullScan.getBarcodesMap();
    assertEquals(array[0][0], map.get("A01"));
    assertEquals(array[1][0], map.get("B01"));
    assertEquals(array[0][1], map.get("A02"));
    assertEquals(array[1][1], map.get("B02"));
  }
  
  @Test
  public void testImmutability() {
    BoxScan fullScan = getFullScan();
    
    String[][] array = fullScan.getBarcodesArray();
    assertEquals(array[0][0], "11111");
    
    Map<String, String> map = fullScan.getBarcodesMap();
    assertEquals(array[0][0], map.get("A01"));
    
    array[0][0] = "changed";
    map.put("A01", "changed");
    
    array = fullScan.getBarcodesArray();
    assertEquals(array[0][0], "11111");
    
    map = fullScan.getBarcodesMap();
    assertEquals(array[0][0], map.get("A01"));
  }
  
  @Test
  public void testFullScan() {
    BoxScan fullScan = getFullScan();
    assertTrue(fullScan.isFull());
    assertFalse(fullScan.isEmpty());
    assertEquals(fullScan.getColumnCount(), 2);
    assertEquals(fullScan.getRowCount(), 2);
    assertEquals(fullScan.getMaximumTubeCount(), 4);
    assertEquals(fullScan.getTubeCount(), 4);
    assertFalse(fullScan.hasReadErrors());
    
  }
  
  @Test
  public void testEmptyScan() {
    BoxScan emptyScan = getEmptyScan();
    assertFalse(emptyScan.isFull());
    assertTrue(emptyScan.isEmpty());
    assertEquals(emptyScan.getColumnCount(), 2);
    assertEquals(emptyScan.getRowCount(), 2);
    assertEquals(emptyScan.getMaximumTubeCount(), 4);
    assertEquals(emptyScan.getTubeCount(), 0);
    assertFalse(emptyScan.hasReadErrors());
  }
  
  @Test
  public void testFullScanWithReadError() {
    BoxScan erredScan = getErredScan();
    assertTrue(erredScan.isFull());
    assertFalse(erredScan.isEmpty());
    assertEquals(erredScan.getColumnCount(), 2);
    assertEquals(erredScan.getRowCount(), 2);
    assertEquals(erredScan.getMaximumTubeCount(), 4);
    assertEquals(erredScan.getTubeCount(), 4);
    assertTrue(erredScan.hasReadErrors());
    List<String> errPositions = erredScan.getReadErrorPositions();
    assertEquals(errPositions.size(), 2);
    assertTrue("A02".equals(errPositions.get(0)) || "A02".equals(errPositions.get(1)));
    assertTrue("B01".equals(errPositions.get(0)) || "B01".equals(errPositions.get(1)));
  }
  
}
