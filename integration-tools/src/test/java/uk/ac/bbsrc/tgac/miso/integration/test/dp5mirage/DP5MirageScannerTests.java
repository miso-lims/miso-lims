package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

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
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

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
  public static void setup() throws IntegrationException {
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
  protected DP5MirageScanner getScanner() throws IntegrationException {
    return client;
  }

  @Override
  protected void simulateScan(BoxScan scan) {
    // Return a specific canned HTTP response, depending on the barcode in first position [1,1]
    if (scan.getBarcode(1, 1).equals("11111")) {
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
    }
    else if (scan.getBarcode(1, 1).equals("22222")) {
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
    }
    else if (scan.getBarcode(1, 1).equals("33333")) {
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
    }
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