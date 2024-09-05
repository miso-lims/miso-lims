package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner;
import uk.ac.bbsrc.tgac.miso.integration.test.BoxScannerTests;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

public class DP5MirageScannerTests extends BoxScannerTests<DP5MirageScanner> {

  /**
   * @return a BoxScanner to test with. This may be a static object
   */
  @Override
  protected DP5MirageScanner getScanner() throws IntegrationException {
    return null;
  }

  /**
   * Simulates a scan as if an end user has used the box scanner to scan a box of tubes
   *
   * @param scan a reference BoxScan to simulate
   */
  @Override
  protected void simulateScan(BoxScan scan) {

  }

  /**
   * Returns a new BoxScan representing a scan of a 2x2 box with the specified barcode in position A01
   *
   * @param barcode the barcode to include in position A01
   * @return the new BoxScan
   */
  @Override
  protected BoxScan getSampleScan(String barcode) {
    return null;
  }

  /**
   * This method is called before {@link BoxScanner#prepareScan(int, int)} in tests incase any mock setup needs to be done
   */
  @Override
  protected void prePrepare() {

  }

  /**
   * This method is called before {@link BoxScanner#getScan()} in tests incase any mock setup needs to be done
   */
  @Override
  protected void preGet() {

  }
}
