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
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  protected static final Logger log = LoggerFactory.getLogger(DP5MirageScanner.class);

  public record DP5MirageScanPosition (String barcode, String decodeStatus, int y, int x, int row,
                              int column) {}

  /**
   * Constructs a new DP5MirageScanner to communicate with a DP5-headless server and retrieve scan
   * data
   *
   * @param host DP5Mirage server hostname or ip
   * @param port DP5Mirage server port
   */
  public DP5MirageScanner(String host, int port) {
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
        if (response.statusCode() == 453) {
          throwScanError(String.format("Container is not found. Check that a container with the "
                  + "container ID: %s is created on the DP5 application", DP5MirageScanner.params.get(
                      "container_uid")),
              true);
        }
        else if (response.statusCode() == 456) {
          throwScanError("Scanner type mismatch", false);
        }
        else if (response.statusCode() == 459) {
          throwScanError("The scan is not found. Try rescanning after a short period of time",
              true);
        }
        else if (response.statusCode() == 461) {
          throwScanError("The Scanner is not connected. Please check the connection before "
              + "rescanning",true);
        }
        else if (response.statusCode() == 468) {
          throwScanError("Scan result is not ready" ,false);
        }
        else if (response.statusCode() == 477) {
          throwScanError("Failed to read a barcode" ,false);
        }
        else if (response.statusCode() == 478) {
          throwScanError("Scanner not found" ,false);
        }
        else if (response.statusCode() == 488) {
          throwScanError("Linear Reader is not configured" ,false);
        }
        else {
          throwScanError("", false);
        }
      }

      // Parse JSON into a JsonNode
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(response.body());

      // Convert the 'barcodes' array into a list of barcodePositionData records
      records = mapper.convertValue(rootNode.get("tubeBarcode"), new TypeReference<>() {});

    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new IntegrationException("Error communicating with the scanner", e);
    }

    return records == null ? null : new DP5MirageScan(records);
  }

  // Used to help determine what the problem is for an unexpected scan response
  private static void throwScanError(String specificMessage, boolean reportSpecific)
      throws IntegrationException {
    if (reportSpecific) {
      throw new IntegrationException("Error Reported by the DP5 scanner. " + specificMessage);
    } else {
      log.info("Timestamp: {}", new Date());
      log.error("Scan error reported by DP5 scanner: {}", specificMessage);
      throw new IntegrationException("Error Reported by the DP5 scanner.");
    }
  }
}