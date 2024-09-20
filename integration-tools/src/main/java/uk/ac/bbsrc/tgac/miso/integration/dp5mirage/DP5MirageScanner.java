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
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScan;


/**
 * This class integrates the MirageGP5 scanner with MISO, and involves communicating with
 * the scanner software via API.
 * For proper usage, see {@link BoxScanner}
 */
public class DP5MirageScanner implements BoxScanner {

  private String[] args;
  private String host;
  private int port;
  private HttpClient httpClient;
  private int getParamTimeout = 10;
  private int postActionTimeout = 10;
  private int putActionTimeout = 10;
  private int deleteActionTimeout = 10;
  private String REMOTE_STUB = "/dp5/remote/v1";
  private HttpRequest request;

  // Setters
  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public void setGetParamTimeout(int getParamTimeout) {
    this.getParamTimeout = getParamTimeout;
  }

  public void setPostActionTimeout(int postActionTimeout) {
    this.postActionTimeout = postActionTimeout;
  }

  public void setDeleteActionTimeout(int deleteActionTimeout) {
    this.deleteActionTimeout = deleteActionTimeout;
  }

  // Getters
  public HttpClient getHttpClient() {
    return this.httpClient;
  }

  private String getParameterAppend(Map<String,String> parameters) {
    String appendQuery = "";

    for (Entry<String, String> entry : parameters.entrySet()) {
      appendQuery = appendQuery + entry.getKey() + "=" + entry.getValue() + "&";
    }
    return appendQuery;
  }

  /**
   * Sending a GET request
   * @param action
   * @param urlStub
   * @param parameters
   * @return
   * @throws URISyntaxException
   */
  private HttpRequest getGetParamRequest(String action, String urlStub, Map<String,String> parameters) throws URISyntaxException
  {
    URI uri = URI.create(urlStub + action);

    if(parameters != null) {
      String queryParam = getParameterAppend(parameters);
      uri= new URI(uri.getScheme(), uri.getAuthority(),
          uri.getPath(), queryParam, uri.getFragment());

    }

    request = HttpRequest.newBuilder().uri(uri)
        .timeout(Duration.ofSeconds(getParamTimeout))
        .GET().build();
    return request;
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
   * Sending a PUT request
   * @param action
   * @param urlStub
   * @return
   * @throws URISyntaxException
   */
  private HttpRequest getPutParamRequest(String action, String urlStub) throws URISyntaxException
  {
    URI uri = URI.create(urlStub + action);
    request = HttpRequest.newBuilder().uri(uri)
        .timeout(Duration.ofSeconds(putActionTimeout))
        .PUT(BodyPublishers.ofString("")).build();
    return request;
  }

  /**
   * Constructs a new DP5MirageScanner to communicate with a DP5-headless server and retrieve scan
   * data
   * <p>
   * Initialise the host and port which is localhost and 8777 by default
   * @throws IntegrationException if the server hostname or ip could not be resolved
   */
  public DP5MirageScanner() throws IntegrationException {
    this.host = "localhost";
    this.port = 8777;
    this.httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();
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
    this.httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();
  }

  /**
   * Constructs a new DP5MirageScanner to communicate with a DP5-headless server and retrieve scan
   * data
   *
   * @param host DP5Mirage server hostname or ip
   * @param port DP5Mirage server port
   * @param httpClient DP5Mirage httpClient used for testing
   * @throws IntegrationException if the server hostname or ip could not be resolved
   */
  public DP5MirageScanner(String host, int port, HttpClient httpClient) throws IntegrationException {
    this.host = host;
    this.port = port;
    this.httpClient = httpClient;
  }

  // Helper Methods
  private String urlStub() {
    return String.format("http://%s:%s"+REMOTE_STUB, host,port);
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
      response = this.getHttpClient().send(getPostParamRequest("/scan", urlStub(), parameters),
          BodyHandlers.ofString());

      // Store results
      ObjectMapper mapper = new ObjectMapper();
      scanResults = mapper.readTree(response.body());

    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new IntegrationException("Error communicating with the scanner", e);
    }
    if (response.statusCode() != 200) {
      // Throw error of incorrect output
      throw new IntegrationException("Error reported by the scanner");
    }
    return scanResults;
  }

  /**
   * Shutdown the DP5 headless
   */
  public void shutDown() throws IntegrationException {
    HttpResponse<String> response=null;
    try {
      response = this.getHttpClient().send(getPutParamRequest("/system/shutdown", urlStub()),
          BodyHandlers.ofString());
    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new IntegrationException("Error communicating with the scanner", e);
    }
    assert response != null;
    if (response.statusCode() != 200) {
      // Throw error of incorrect output
      throw new IntegrationException("Error reported by the scanner");
    }
  }

  /**
   * Performs any setup actions required to ensure that the scanner is prepared to scan a box of the correct size. Calling this method
   * multiple times will have the same effect. Failing to call this method before {@link #getScan()} may have undesirable consequences
   * such as scan failure or returning of old or inaccurate data. This is a blocking call and returns when the scanner is confirmed ready
   * to scan. Failing to provide the correct box dimensions may result in inaccurate or incomplete data
   *
   * @param expectedRows number of rows to expect in the rack to be scanned
   * @param expectedColumns number of columns to expect in the rack being scanned
   * @throws IntegrationException if the scanner cannot be accessed or any hardware-specific error occurs
   */
  @Override
  public void prepareScan(int expectedRows, int expectedColumns) throws IntegrationException {
    // No preparation needed before scanning
  }

  /**
   * Retrieves scanned barcode data from the scanner. {@link #prepareScan(int expectedRows, int expectedColumns)} must be called before this method to initialize the
   * scanner and do things such as clearing old data to ensure that this method returns fresh results. This is a blocking call and
   * returns when the scanner provides scan data or the operation times out
   *
   * @return the scan data, or null if no scan is completed before the operation times out
   * @throws IntegrationException if the scanner cannot be accessed or any hardware-specific error occurs
   */
  @Override
  public BoxScan getScan() throws IntegrationException{
    // Scan a rack in DP5 for a given container uid
    Map<String, String> params = new HashMap<>();
    params.put("container_uid", "mirage96sbs"); //TODO ADD THIS TO DOCUMENTATION
    params.put("annotated_image", "false");
    params.put("raw_image", "false");
    JsonNode scan = scanRackUsingDP5(params);

    return scan == null ? null : new DP5MirageScan(scan);
  }
}