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

      String errorMessage = "Error reported by the scanner.";
      String actionMessage = "please check this before rescanning.";

      // Check if valid before parsing
      if (response.statusCode() != 200) {
        if (response.statusCode() == 453) {
          throw new IntegrationException(String.format(" %s Container is not found. Check that a "
              + "container with the"
              + " container ID: %s is created on the DP5 application", errorMessage,
              DP5MirageScanner.params.get(
              "container_uid")));
        }
        else if (response.statusCode() == 456) {
          throw new IntegrationException(String.format("%s Scanner type mismatch. %s",
              errorMessage, actionMessage));
        }
        else if (response.statusCode() == 459) {
          throw new IntegrationException(String.format("%s The scan is not found. Try rescanning "
              + "after a short period of time.", errorMessage));
        }
        else if (response.statusCode() == 461) {
          throw new IntegrationException(String.format("%s Scanner not connected. %s",
              errorMessage, actionMessage));
        }
        else if (response.statusCode() == 468) {
          throw new IntegrationException(String.format("%s Scan result is not ready. Try "
                  + "rescanning after a short period of time.",
              errorMessage));
        }
        else if (response.statusCode() == 477) {
          throw new IntegrationException(String.format("%s Failed to read a barcode. %s",
              errorMessage, actionMessage));
        }
        else if (response.statusCode() == 478) {
          throw new IntegrationException(String.format("%s Scanner not found. %s", errorMessage,
              actionMessage));
        }
        else if (response.statusCode() == 488) {
          throw new IntegrationException(String.format("%s Linear Reader is not configured. %s",
              errorMessage, actionMessage));
        }
        else {
          throw new IntegrationException(errorMessage);
        }
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