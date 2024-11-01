package uk.ac.bbsrc.tgac.miso.integration.dp5mirage;

import static uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils.GetPostParamRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse;
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

  @Override
  public void prepareScan(int expectedRows, int expectedColumns) throws IntegrationException {
    // No preparation needed before scanning
  }

  @Override
  public BoxScan getScan() throws IntegrationException{
    HttpResponse<String> response;
    List<DP5MirageScanPosition> records;
    try {
      URI uri = URI.create(String.format("http://%s:%s/dp5/remote/v1/scan", host, port));

      // JSON response from Scanner
      response = GetPostParamRequest(httpClient, uri, DP5MirageScanner.params);

      // Check if valid before parsing
      if (response.statusCode() != 200) {
        if (response.statusCode() == 488) {
          throw new IntegrationException("Linear Reader is not configured or connected. Please "
              + "check this before rescanning.");
        }
        else if (response.statusCode() == 459 || response.statusCode() == 468) {
          throw new IntegrationException("The scan is not found or the scan result is not ready.");
        }
        else if (response.statusCode() == 477) {
          throw new IntegrationException("Failed to read a barcode.");
        }

        throw new IntegrationException(String.format("Error reported by the scanner. \n "
                + "Check that a container with the container ID: %s is created on the DP5 "
            + "application. \n There could be a scanner type mismatch or the scanner is not "
            + "connected, please check these before rescanning.", DP5MirageScanner.params.get(
                "container_uid")));
      }

      // Parse JSON into a JsonNode
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(response.body());
      JsonNode barcodesNode = rootNode.get("tubeBarcode");

      // Convert the 'barcodes' array into a list of barcodePositionData records
      records = mapper.convertValue(barcodesNode, new TypeReference<>() {});

    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new IntegrationException("Error communicating with the scanner", e);
    }

    return records == null ? null : new DP5MirageScan(records);
  }
}