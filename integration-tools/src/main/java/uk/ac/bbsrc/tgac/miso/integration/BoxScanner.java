package uk.ac.bbsrc.tgac.miso.integration;

import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

/**
 * Controls a multi-tube barcode reader to read the barcodes of all tubes in a box. Usage involves two steps. First, call 
 * {@link #prepareScan(int, int)} to ensure the scanner is ready to run a new scan of the correct box size, and will not report old data. 
 * Second, call {@link #getScan()} to wait for and retrieve new scan data. The end user can be instructed to scan a box before or after 
 * {@link #getScan()} is called.
 * <p>
 * Example usage:
 * <p>
 * <pre>
 * {@code
 *  BoxScanner scanner = new ...
 *  scanner.prepareScan(8, 12);
 *  // ( instruct user to scan the box )
 *  scanner.getScan();
 * }
 * </pre>
 */
public interface BoxScanner {
  
  /**
   * Performs any setup actions required to ensure that the scanner is prepared to scan a box of the correct size. Calling this method 
   * multiple times will have the same effect. Failing to call this method before {@link #getScan()} may have undesirable consequences 
   * such as scan failure or returning of old or inaccurate data. This is a blocking call and returns when the scanner is confirmed ready 
   * to scan. Failing to provide the correct box dimensions may result in inaccurate or incomplete data
   * 
   * @param expectedRows number of rows to expect in the rack to be scanned
   * @param expectedColumns number of columns to expect in the rack being scanned
   * @throws IntegrationException if the scanner cannot be accessed or any hardware-specific error occurs
   */
  public void prepareScan(int expectedRows, int expectedColumns) throws IntegrationException;
  
  /**
   * Retrieves scanned barcode data from the scanner. {@link #prepareScan()} must be called before this method to initialize the 
   * scanner and do things such as clearing old data to ensure that this method returns fresh results. This is a blocking call and 
   * returns when the scanner provides scan data or the operation times out
   * 
   * @return the scan data, or null if no scan is completed before the operation times out
   * @throws IntegrationException if the scanner cannot be accessed or any hardware-specific error occurs
   */
  public BoxScan getScan() throws IntegrationException;
  
}
