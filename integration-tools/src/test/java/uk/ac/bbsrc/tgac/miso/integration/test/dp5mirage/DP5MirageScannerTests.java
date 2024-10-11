package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner.DP5MirageScanPosition;
import uk.ac.bbsrc.tgac.miso.integration.test.BoxScannerTests;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.WireMockServer;

public class DP5MirageScannerTests extends BoxScannerTests<DP5MirageScanner> {
  private static WireMockServer server;
  private static DP5MirageScanner client;

  @BeforeClass
  public static void setup() throws IntegrationException {
    // Set up the WireMock server on a dynamic port
    server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    server.start();

    // Configure wiremock to mock responses
    configureFor("localhost", server.port());

    client = new DP5MirageScanner("localhost", 9023); //TODO host and port
  }

  @AfterClass
  public static void shutdown() {
    server.shutdown();
  }

  // Return a BoxScanner to test with. This may be a static object
  @Override
  protected DP5MirageScanner getScanner() throws IntegrationException {
    return client;
  }

  // Simulates a scan as if an end user has used the box scanner to scan a box of tubes
  @Override
  protected void simulateScan(BoxScan scan) {
    int rows = scan.getRowCount();
    int cols = scan.getColumnCount();

    String[] barcodes = new String[rows*cols];
    int i = 0;
    for (int col = 1; col <= cols; col++) {
      for (int row = 1; row <= rows; row++) {
        barcodes[i] = scan.getBarcode(row, col);
        i++;
      }
    }
  }

  // return new BoxScan representing a scan of a 2x2 box with specified barcode in position A01
  @Override
  protected BoxScan getSampleScan(String barcode) {
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition(barcode, "SUCCESS", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("33333", "SUCCESS", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("22222", "SUCCESS", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("44444", "SUCCESS", 2, 2, 2, 2));

    return new DP5MirageScan(records);
  }

  @Override
  protected void prePrepare() {
  }

  @Override
  protected void preGet() {
  }

  //TODO might not be needed
  private BoxScan emptyMock() {
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition("null", "EMPTY", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("null", "EMPTY", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("null", "EMPTY", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("null", "EMPTY", 2, 2, 2, 2));

    return new DP5MirageScan(records);
  }
}