package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner.DP5MirageScanPosition;
import uk.ac.bbsrc.tgac.miso.integration.test.BoxScannerTests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class DP5MirageScannerTests extends BoxScannerTests<DP5MirageScanner> {
  private static WireMockServer server;
  private static DP5MirageScanner client;

  @BeforeClass
  public static void setup() {
    server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    server.start();
    WireMock.configureFor("localhost", server.port());
    client = new DP5MirageScanner("localhost", server.port());
  }

  @AfterClass
  public static void shutdown() {
    server.shutdown();
  }

  @Override
  protected DP5MirageScanner getScanner() {
    return client;
  }

  @Override
  protected void simulateScan(BoxScan scan) {
    // Create the scan object node
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode scanObjectNode = mapper.createObjectNode();
    ArrayNode tubeBarcodeArrayNode = mapper.createArrayNode();

    // Create all the fields required from a scan
    scanObjectNode.put("scanId", "3fa85f64-5717-4562-b3fc-2c963f66afa6");
    scanObjectNode.put("scanTime", "2024-10-07T19:20:17.413Z");
    scanObjectNode.put("containerBarcode", "container-barcode");
    scanObjectNode.put("scanTimeAnswers", "null");
    scanObjectNode.put("containerName", "96 SBS rack");
    scanObjectNode.put("containerBarcode", "empty-container-barcode");
    scanObjectNode.put("containerUid", "mirage96sbs");
    scanObjectNode.put("demoImage", "null");
    scanObjectNode.put("containerGuid", "f156992b-36c7-7987-3a78-2012542ta2e");
    scanObjectNode.put("rawImage", "null");
    scanObjectNode.put("annotatedImage", "null");
    scanObjectNode.put("linearReaderImage", "null");

    // Grab row and column information from the test scan
    int rows = scan.getRowCount();
    int cols = scan.getColumnCount();
    for (int col = 1; col <= cols; col++) {
      for (int row = 1; row <= rows; row++) {
        // create ObjectNode for each tubeBarcode position
        ObjectNode positionObjectNode = mapper.createObjectNode();
        positionObjectNode.put("row", row);
        positionObjectNode.put("y", col);
        positionObjectNode.put("x", row);
        // Get the decode status
        if (scan.getBarcode(row, col).equals("No Tube")) {
          positionObjectNode.put("decodeStatus", "EMPTY");
        }
        else if (scan.getBarcode(row, col).equals("No Read")) {
          positionObjectNode.put("decodeStatus","ERROR");
        }
        else {
          positionObjectNode.put("decodeStatus","SUCCESS");
        }
        positionObjectNode.put("column", col);
        positionObjectNode.put("barcode", scan.getBarcode(row, col));

        // Add the position to the arrayNode
        tubeBarcodeArrayNode.add(positionObjectNode);
      }
    }
    scanObjectNode.put("tubeBarcode", tubeBarcodeArrayNode);

    // Create the stubbed mock response
    stubFor(post(urlEqualTo("/dp5/remote/v1/scan?container_uid=mirage96sbs"))
    .willReturn(aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody(scanObjectNode.toString())));
  }

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
    // Default stub post request that mocks an empty response
    stubFor(post(urlEqualTo("/dp5/remote/v1/scan?container_uid=mirage96sbs"))
        .willReturn(aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/json")
              .withBody("")));
  }

  @Override
  protected void preGet() {
    // No preGet needed
  }
}