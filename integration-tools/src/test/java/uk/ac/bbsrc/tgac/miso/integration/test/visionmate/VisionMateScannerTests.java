package uk.ac.bbsrc.tgac.miso.integration.test.visionmate;

import org.junit.BeforeClass;
import org.junit.Ignore;

import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.test.BoxScannerTests;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScan;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScanner;

import ca.on.oicr.gsi.visionmate.RackType;
import ca.on.oicr.gsi.visionmate.RackType.Manufacturer;
import ca.on.oicr.gsi.visionmate.Scan;
import ca.on.oicr.gsi.visionmate.ServerConfig;
import ca.on.oicr.gsi.visionmate.mockServer.MockScannerServer;

// TODO: re-enable after server port-assignment has been "randomized" to prevent collisions
@Ignore
public class VisionMateScannerTests extends BoxScannerTests<VisionMateScanner> {
  
  private static final int SERVER_PORT = 8503;

  private static MockScannerServer server;
  private static VisionMateScanner client;
  
  private static Thread serverThread;
  
  @BeforeClass
  public static void setup() throws IntegrationException {
    server = new MockScannerServer(SERVER_PORT);
    client = new VisionMateScanner("127.0.0.1", SERVER_PORT, 2000, 5000);
  }
  
  @Override
  protected VisionMateScanner getScanner() throws IntegrationException {
    return client;
  }

  @Override
  protected void simulateScan(BoxScan scan) {
    int rows = scan.getRowCount();
    int cols = scan.getColumnCount();
    server.setCurrentProduct(new RackType(Manufacturer.MATRIX, rows, cols));
    String[] barcodes = new String[rows*cols];
    int i = 0;
    for (int col = 1; col <= cols; col++) {
      for (int row = 1; row <= rows; row++) {
        barcodes[i] = scan.getBarcode(row, col);
        i++;
      }
    }
    server.emulateScan(barcodes);
  }

  @Override
  protected BoxScan getSampleScan(String barcode) {
    RackType rack = new RackType(Manufacturer.MATRIX, 2, 2);
    ServerConfig config = new ServerConfig();
    String data = barcode + ",22222,33333,44444,";
    Scan wrappedScan = new Scan(rack, data, config);
    return new VisionMateScan(wrappedScan);
  }

  @Override
  protected void prePrepare() {
    prepareServer();
  }

  @Override
  protected void preGet() {
    prepareServer();
  }
  
  private void prepareServer() {
    if (serverThread != null) {
      try {
        serverThread.join();
      } catch (InterruptedException e) {
        throw new IllegalStateException("Wait for server availability failed");
      }
    }
    serverThread = new Thread(server);
    serverThread.start();
    try {
      // sleep for a second to give the mock server time to start before the client tries to connect
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // Unhandled. Already retried. Worst-case: unit test fails
      }
    }
  }

}
