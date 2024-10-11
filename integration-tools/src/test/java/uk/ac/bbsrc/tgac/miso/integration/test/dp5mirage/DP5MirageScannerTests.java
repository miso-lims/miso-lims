package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner.DP5MirageScanPosition;
import uk.ac.bbsrc.tgac.miso.integration.test.BoxScannerTests;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class DP5MirageScannerTests extends BoxScannerTests<DP5MirageScanner> {

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults to

  private static WireMockServer wireMockServer;
  private static DP5MirageScanner client;

  @BeforeClass
  public static void setup() throws IntegrationException {
    // Set up the WireMock server on a dynamic port
    wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    wireMockServer.start();

    // Configure wiremock to mock responses
    configureFor("localhost", wireMockServer.port());

    client = new DP5MirageScanner("localhost", 9023);
  }

  //TODO return a BoxScanner to test with. This may be a static object
  @Override
  protected DP5MirageScanner getScanner() throws IntegrationException {
    return client;
  }

  // TODO Simulates a scan as if an end user has used the box scanner to scan a box of tubes
  @Override
  protected void simulateScan(BoxScan scan) {
    int rows = scan.getRowCount();
    int cols = scan.getColumnCount();

    // barcodes array
    String[] barcodes = new String[rows*cols];
    int i = 0;
    for (int col = 1; col <= cols; col++) {
      for (int row = 1; row <= rows; row++) {
        barcodes[i] = scan.getBarcode(row, col);
        i++;
      }
    }
  }

  // TODO return new boxscan representing a scan of a 2x2 box with specified barcode in position A01
  @Override
  protected BoxScan getSampleScan(String barcode) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode wrappedScan = null;
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition(barcode, "SUCCESS", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("33333", "SUCCESS", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("22222", "SUCCESS", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("44444", "SUCCESS", 2, 2, 2, 2));

    //TODO not used
    String tubeBarcode = "[{\"row\":1,\"column\":1,\"barcode\": " + barcode +", "
        + "\"decodeStatus\":\"SUCCESS"
        + "\",\"x\":1,\"y\":1}, {\"row\":1,\"column\":2,\"barcode\":\"null\", "
        + "\"decodeStatus\":\"EMPTY\",\"x\":1,\"y\":2}, {\"row\":2,\"column\":1,"
        + "\"barcode\":\"null\", \"decodeStatus\":\"EMPTY\",\"x\":2,\"y\":1}, {\"row\":2,"
        + "\"column\":2,\"barcode\":\"null\", \"decodeStatus\":\"EMPTY\",\"x\":2,\"y\":2}]";

    //TODO not used
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
    }
    return new DP5MirageScan(records);
  }

  @Override
  protected void prePrepare() {
  }

  @Override
  protected void preGet() {
  }
}