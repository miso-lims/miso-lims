package uk.ac.bbsrc.tgac.miso.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

public abstract class BoxScannerTests<T extends BoxScanner> {
  
  /**
   * @return a BoxScanner to test with. This may be a static object
   */
  protected abstract T getScanner() throws IntegrationException;
  
  /**
   * Simulates a scan as if an end user has used the box scanner to scan a box of tubes
   * 
   * @param scan a reference BoxScan to simulate
   */
  protected abstract void simulateScan(BoxScan scan);
  
  /**
   * Returns a new BoxScan representing a scan of a 2x2 box with the specified barcode in position A01
   * 
   * @param barcode the barcode to include in position A01
   * @return the new BoxScan
   */
  protected abstract BoxScan getSampleScan(String barcode);
  
  /**
   * This method is called before {@link BoxScanner#prepareScan(int, int)} in tests incase any mock setup needs to be done
   */
  protected abstract void prePrepare();
  
  /**
   * This method is called before {@link BoxScanner#getScan()} in tests incase any mock setup needs to be done
   */
  protected abstract void preGet();
  
  @Test
  public void testScanBeforeGet() throws IntegrationException {
    BoxScanner scanner = getScanner();
    prePrepare();
    scanner.prepareScan(2, 2);
    simulateScan(getSampleScan("11111"));
    preGet();
    BoxScan scan = scanner.getScan();
    assertEquals("11111", scan.getBarcode(1, 1));
  }
  
  @Test
  public void testGetBeforeScan() throws IntegrationException {
    BoxScanner scanner = getScanner();
    prePrepare();
    scanner.prepareScan(2, 2);
    
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(2000);
          simulateScan(getSampleScan("22222"));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      
    }).run();
    
    preGet();
    BoxScan scan = scanner.getScan();
    assertEquals("22222", scan.getBarcode(1, 1));
  }
  
  @Test
  public void testMultiplePrepares() throws IntegrationException {
    BoxScanner scanner = getScanner();
    prePrepare();
    scanner.prepareScan(2, 2);
    prePrepare();
    scanner.prepareScan(2, 2);
    prePrepare();
    scanner.prepareScan(2, 2);
    simulateScan(getSampleScan("33333"));
    preGet();
    BoxScan scan = scanner.getScan();
    assertEquals("33333", scan.getBarcode(1, 1));
  }
  
  @Test
  public void testNoScan() throws IntegrationException {
    BoxScanner scanner = getScanner();
    prePrepare();
    scanner.prepareScan(2, 2);
    preGet();
    BoxScan scan = scanner.getScan();
    assertNull(scan);
  }
}
