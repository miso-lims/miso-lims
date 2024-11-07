package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import com.github.tomakehurst.wiremock.client.WireMock;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.github.tomakehurst.wiremock.WireMockServer;

public class DP5MirageScannerTests extends BoxScannerTests<DP5MirageScanner> {
  private static WireMockServer server;
  private static DP5MirageScanner client;

  @BeforeClass
  public static void setup() throws IntegrationException {
    // Set up the WireMock server on a dynamic port
    server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    server.start();

    // Configure wiremock to listen to the server port
    WireMock.configureFor("localhost", server.port());

    // Create scanner connecting to wiremock port
    client = new DP5MirageScanner("localhost", server.port());
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
    // No prePrepare needed
  }

  @Override
  protected void preGet() {
    // No preGet needed
  }

  @Test
  public void testScanBeforeGet() throws IntegrationException {
    // Stub a post request and provide a mocked response
    stubFor(post(urlEqualTo("/dp5/remote/v1/scan?container_uid=mirage96sbs"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"scanId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"scanTime\": "
                + "\"2024-10-07T19:20:17.413Z\", \"containerBarcode\": "
                + "\"empty-container-barcode\", \"scanTimeAnswers\": null,  "
                + "\"containerName\": \"96 SBS rack\", \"containerUid\": "
                + "\"mirage96sbs\", \"demoImage\": null, \"containerGuid\": "
                + "\"f156992b-36c7-7987-3a78-2012542ta2e\", \"rawImage\": null,  "
                + "\"annotatedImage\": null, \"linearReaderImage\": null, \"tubeBarcode\": [{"
                + "\"row\": 1, \"y\": 0, \"x\": 0, \"decodeStatus\": \"SUCCESS\", "
                + "\"column\": 1, \"barcode\": \"11111\"}, {\"row\": 1, "
                + "\"y\": 0, \"x\": 0, \"decodeStatus\": \"EMPTY\", \"column\": "
                + "2, \"barcode\": \"null\"}, {\"row\": 2, \"y\": 0, "
                + "\"x\": 0, \"decodeStatus\": \"EMPTY\", \"column\": 1, "
                + "\"barcode\": \"null\"}, {\"row\": 2, \"y\": 0, "
                + "\"x\": 0, \"decodeStatus\": \"EMPTY\", \"column\": 2, "
                + "\"barcode\": \"null\"}]}")));

    prePrepare();
    client.prepareScan(2, 2);
    preGet();
    BoxScan scan = client.getScan();
    assertEquals("11111", scan.getBarcode(1, 1));
  }

  @Test
  public void testGetBeforeScan() throws IntegrationException{
    // Stub a post request and provide a mocked response
    stubFor(post(urlEqualTo("/dp5/remote/v1/scan?container_uid=mirage96sbs"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"scanId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"scanTime\": "
                + "\"2024-10-07T19:20:17.413Z\", \"containerBarcode\": "
                + "\"empty-container-barcode\", \"scanTimeAnswers\": null,  "
                + "\"containerName\": \"96 SBS rack\", \"containerUid\": "
                + "\"mirage96sbs\", \"demoImage\": null, \"containerGuid\": "
                + "\"f156992b-36c7-7987-3a78-2012542ta2e\", \"rawImage\": null,  "
                + "\"annotatedImage\": null, \"linearReaderImage\": null, \"tubeBarcode\": [{"
                + "\"row\": 1, \"y\": 0, \"x\": 0, \"decodeStatus\": \"SUCCESS\", "
                + "\"column\": 1, \"barcode\": \"22222\"}, {\"row\": 1, "
                + "\"y\": 0, \"x\": 0, \"decodeStatus\": \"EMPTY\", \"column\": "
                + "2, \"barcode\": \"null\"}, {\"row\": 2, \"y\": 0, "
                + "\"x\": 0, \"decodeStatus\": \"EMPTY\", \"column\": 1, "
                + "\"barcode\": \"null\"}, {\"row\": 2, \"y\": 0, "
                + "\"x\": 0, \"decodeStatus\": \"EMPTY\", \"column\": 2, "
                + "\"barcode\": \"null\"}]}")));

    prePrepare();
    client.prepareScan(2, 2);
    simulateScan(getSampleScan("22222"));
    preGet();

    BoxScan scan = client.getScan();
    assertEquals("22222", scan.getBarcode(1, 1));
  }

  @Test
  public void testMultiplePrepares() throws IntegrationException {
    // Stub a post request and provide a mocked response
    stubFor(post(urlEqualTo("/dp5/remote/v1/scan?container_uid=mirage96sbs"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"scanId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"scanTime\": "
                + "\"2024-10-07T19:20:17.413Z\", \"containerBarcode\": "
                + "\"empty-container-barcode\", \"scanTimeAnswers\": null,  "
                + "\"containerName\": \"96 SBS rack\", \"containerUid\": "
                + "\"mirage96sbs\", \"demoImage\": null, \"containerGuid\": "
                + "\"f156992b-36c7-7987-3a78-2012542ta2e\", \"rawImage\": null,  "
                + "\"annotatedImage\": null, \"linearReaderImage\": null, \"tubeBarcode\": [{"
                + "\"row\": 1, \"y\": 0, \"x\": 0, \"decodeStatus\": \"SUCCESS\", "
                + "\"column\": 1, \"barcode\": \"33333\"}, {\"row\": 1, "
                + "\"y\": 0, \"x\": 0, \"decodeStatus\": \"EMPTY\", \"column\": "
                + "2, \"barcode\": \"null\"}, {\"row\": 2, \"y\": 0, "
                + "\"x\": 0, \"decodeStatus\": \"EMPTY\", \"column\": 1, "
                + "\"barcode\": \"null\"}, {\"row\": 2, \"y\": 0, "
                + "\"x\": 0, \"decodeStatus\": \"EMPTY\", \"column\": 2, "
                + "\"barcode\": \"null\"}]}")));

    client = getScanner();
    prePrepare();
    client.prepareScan(2, 2);
    prePrepare();
    client.prepareScan(2, 2);
    prePrepare();
    client.prepareScan(2, 2);
    simulateScan(getSampleScan("33333"));
    preGet();
    BoxScan scan = client.getScan();
    assertEquals("33333", scan.getBarcode(1, 1));
  }

  @Test
  public void testNoScan() throws IntegrationException {
    // Stub a post request and provide a mocked response
    stubFor(post(urlEqualTo("/dp5/remote/v1/scan?container_uid=mirage96sbs"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("")));
    prePrepare();
    client.prepareScan(2, 2);
    preGet();
    BoxScan scan = client.getScan();
    assertNull(scan);
  }
}