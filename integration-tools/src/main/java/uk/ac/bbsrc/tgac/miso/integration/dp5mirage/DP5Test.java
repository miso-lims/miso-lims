package uk.ac.bbsrc.tgac.miso.integration.dp5mirage;

import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

public class DP5Test {
  public static void main(String[] args) throws IntegrationException {
    DP5MirageScanner scanner = new DP5MirageScanner("localhost", 8777);

    // Test Scanning
    scanner.getScan();
  }
}