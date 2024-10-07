package uk.ac.bbsrc.tgac.miso.integration.dp5mirage;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScan;


/**
 * This class integrates the MirageDP5 scanner with MISO, and involves communicating with
 * the scanner software via API.
 * For proper usage, see {@link BoxScanner}
 */
public class DP5MirageScanner implements BoxScanner {

  private final String host;
  private final int port;
  private HttpClient httpClient;
  private HttpRequest request;
  private static final int postActionTimeout = 10;
  private static final String REMOTE_STUB = "/dp5/remote/v1";
  private static final Map<String, String> params = new HashMap<String, String>() {{put(
      "container_uid", "mirage96sbs");}};

  // Getters
  private String getParameterAppend(Map<String,String> parameters) {
    return parameters.entrySet().stream().map(e -> new StringBuilder(e.getKey()).append("=").append(e.getValue()).toString()).collect(
        Collectors.joining("&"));
  }

  /**
   * Sending a POST request
   * @param action
   * @param urlStub
   * @param parameters
   * @return
   * @throws URISyntaxException
   */
  private HttpRequest getPostParamRequest(String action, String urlStub, Map<String,String> parameters) throws URISyntaxException
  {
    URI uri = URI.create(urlStub + action);
    if(parameters != null) {
      String queryParam = getParameterAppend(parameters);
      uri= new URI(uri.getScheme(), uri.getAuthority(),
          uri.getPath(), queryParam, uri.getFragment());
    }
    request = HttpRequest.newBuilder().uri(uri)
        .timeout(Duration.ofSeconds(postActionTimeout))
        .POST(BodyPublishers.ofString("")).build();
    return request;
  }

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
    httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();
  }

  // Helper Methods
  private String urlStub() {
    return String.format("http://%s:%s%s", host,port,REMOTE_STUB);
  }

  /**
   * Returns the scan result as a JsonNode
   * The swagger documentation details out the ScanResult in the schemas section.
   * @return
   */
  private JsonNode scanRackUsingDP5(Map<String,String> parameters) throws IntegrationException {
    HttpResponse<String> response = null;
    JsonNode scanResults = null;
    try {
      // JSON response from Scanner
      response = httpClient.send(getPostParamRequest("/scan", urlStub(), parameters),
          BodyHandlers.ofString());

      // Store results
      ObjectMapper mapper = new ObjectMapper();
      scanResults = mapper.readTree(response.body());

    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new IntegrationException("Error communicating with the scanner",
          e);
    }
    if (response.statusCode() != 200) {
      throw new IntegrationException(String.format("Error reported by the scanner \n "
              + "Check if this container_uid parameter is correct: %s",
          parameters.get("container_uid")));
    }
    return scanResults;
  }

  @Override
  public void prepareScan(int expectedRows, int expectedColumns) throws IntegrationException {
    // No preparation needed before scanning
  }

  @Override
  public BoxScan getScan() throws IntegrationException{
    // Scan a rack in DP5 for a given container uid
    JsonNode scan = scanRackUsingDP5(params);

    return scan == null ? null : new DP5MirageScan(scan);
  }
}