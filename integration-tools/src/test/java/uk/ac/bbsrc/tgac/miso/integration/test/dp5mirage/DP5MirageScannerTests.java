package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner;
import uk.ac.bbsrc.tgac.miso.integration.test.BoxScannerTests;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;


public class DP5MirageScannerTests extends BoxScannerTests<DP5MirageScanner> {

  private static DP5MirageScanner client;

  @BeforeClass
  public static void setup() throws IntegrationException {
    //TODO mock scanner created
    client = new DP5MirageScanner();
  }

  /**
   * @return a BoxScanner to test with. This may be a static object
   */
  @Override
  protected DP5MirageScanner getScanner() throws IntegrationException {
    return client;
  }

  /**
   * Simulates a scan as if an end user has used the box scanner to scan a box of tubes
   *
   * @param scan a reference BoxScan to simulate
   */
  @Override
  protected void simulateScan(BoxScan scan) {
    int rows = scan.getRowCount();
    int cols = scan.getColumnCount();
    //TODO mock server

    // barcodes array
    String[] barcodes = new String[rows*cols];
    int i = 0;
    for (int col = 1; col <= cols; col++) {
      for (int row = 1; row <= rows; row++) {
        barcodes[i] = scan.getBarcode(row, col);
        i++;
      }
    }
    //TODO emulate the scan with the barcodes
  }

  /**
   * Returns a new BoxScan representing a scan of a 2x2 box with the specified barcode in position A01
   *
   * @param barcode the barcode to include in position A01
   * @return the new BoxScan
   */
  @Override
  protected BoxScan getSampleScan(String barcode) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode wrappedScan = null;

    String tubeBarcode = "[{\"row\":1,\"column\":1,\"barcode\": " + barcode +", "
        + "\"decodeStatus\":\"SUCCESS"
        + "\",\"x\":1,\"y\":1}, {\"row\":1,\"column\":2,\"barcode\":\"null\", "
        + "\"decodeStatus\":\"EMPTY\",\"x\":1,\"y\":2}, {\"row\":2,\"column\":1,"
        + "\"barcode\":\"null\", \"decodeStatus\":\"EMPTY\",\"x\":2,\"y\":1}, {\"row\":2,"
        + "\"column\":2,\"barcode\":\"null\", \"decodeStatus\":\"EMPTY\",\"x\":2,\"y\":2}]";

    try {
      wrappedScan = mapper.readTree(
          "{\"scanId\":\"185eea49-0a7a-4c53-9e46-19929234d792\", \"scanTime\":1725379428062, "
              + "\"containerBarcode\":\"#container-barcode\", "
              + "\"scanTimeAnswers\":\"null\", \"containerName\":\"96 SBS rack\", "
              + "\"containerUid\":\"mirage96sbs\", \"demoImage\":\"null\", "
              + "\"containerGuid\":\"1e20491b-b8ba-4c35-991e-2012542f6a5e\", \"rawImage\":\"null\", \"annotatedImage\":\"null\", "
              + "\"linearReaderImage\":\"null\", \"tubeBarcode\":" + tubeBarcode + ", \"orientationBarcode\":[]}"
      );
    } catch (JsonProcessingException e) {
      // TODO
    }
    return new DP5MirageScan(wrappedScan);
  }

  /**
   * This method is called before {@link BoxScanner#prepareScan(int, int)} in tests incase any mock setup needs to be done
   */
  @Override
  protected void prePrepare() {
    //TODO
  }

  /**
   * This method is called before {@link BoxScanner#getScan()} in tests incase any mock setup needs to be done
   */
  @Override
  protected void preGet() {
    //TODO
  }
}