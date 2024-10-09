package uk.ac.bbsrc.tgac.miso.integration.dp5mirage;

import static uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils.getPostParamRequest;

import ca.on.oicr.gsi.visionmate.Scan;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collections;
import java.util.List;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * This class integrates the MirageDP5 scanner with MISO, and involves communicating with
 * the scanner software via API.
 * For proper usage, see {@link BoxScanner}
 */

public class DP5MirageScanner implements BoxScanner {

  private final String host;
  private final int port;
  private final HttpClient httpClient;
  private static final String REMOTE_STUB = "/dp5/remote/v1";
  private static final Map<String, String> params = Collections.singletonMap("container_uid",
      "mirage96sbs");

  public record DP5MirageScanPosition (String barcode, String decodeStatus, int y, int x, int row,
                              int column) {}

  /**
   * Constructs a new DP5MirageScanner to communicate with a DP5-headless server and retrieve scan
   * data
   *
   * @param host DP5Mirage server hostname or ip
   * @param port DP5Mirage server port
   * @throws IntegrationException if the server hostname or ip could not be resolved
   */
  public DP5MirageScanner(String host, int port) throws IntegrationException {
    this.host = host;
    this.port = port;
    this.httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();
  }

  // Helper Methods
  private String urlStub() {
    return String.format("http://%s:%s%s", host,port,REMOTE_STUB);
  }

  @Override
  public void prepareScan(int expectedRows, int expectedColumns) throws IntegrationException {
    // No preparation needed before scanning
  }

  @Override
  public BoxScan getScan() throws IntegrationException{
    HttpResponse<String> response;
    List<DP5MirageScanPosition> records;
    try {
      // JSON response from Scanner
      response = httpClient.send(getPostParamRequest("/scan", urlStub(), DP5MirageScanner.params),
          BodyHandlers.ofString());

      // Parse JSON into a JsonNode
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(response.body());
      JsonNode barcodesNode = rootNode.get("tubeBarcode");

      // Convert the 'barcodes' array into a list of barcodePositionData records
      records = mapper.convertValue(barcodesNode, new TypeReference<>() {});

    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new IntegrationException("Error communicating with the scanner", e);
    }
    if (response.statusCode() != 200) {
      throw new IntegrationException(String.format("Error reported by the scanner \n "
              + "Check if this container_uid parameter is correct: %s",
          DP5MirageScanner.params.get("container_uid")));
    }

    return records == null ? null : new DP5MirageScan(records);
  }
}