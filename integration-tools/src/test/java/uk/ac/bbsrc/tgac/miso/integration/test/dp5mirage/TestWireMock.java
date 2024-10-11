package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestWireMock {
  private WireMockServer wiremockServer;

  @Before
  public void setup() {
    //start wiremock server on a dynamic port
    wiremockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    wiremockServer.start();

    // configure wiremock to listen to the server port
    WireMock.configureFor("localhost", wiremockServer.port());
  }

  @After
  public void shutdown() {
    //shutdown the wiremock server once tests conclude
    wiremockServer.shutdown();
  }

  @Test
  public void testPostRequest() throws Exception {
    // Stub a post request and provide a mocked response
    stubFor(post(urlEqualTo("/api/test"))
        .withHeader("Content-Type", equalTo("application/json"))
        .withRequestBody(equalToJson("{\"name\":\"John\"}"))
        .willReturn(aResponse()
            .withStatus(201)
            .withHeader("Content-type", "application/json")
            .withBody("{\"id\":123,\"message\":\"Created\"}")));

    // Define the URI using wiremock dynamic port
    URI uri = new URI("http://localhost:" + wiremockServer.port() + "/api/test");

    // create an HttpClient and HttpRequest for the test
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .timeout(Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString("{\"name\":\"John\"}"))
        .build();

    // Send the request and get the response
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    //verify that the response is as expected
    assertEquals(201, response.statusCode());
    assertEquals("{\"id\":123,\"message\":\"Created\"}", response.body());

    // verify that the POST request was made exactly once
    verify(postRequestedFor(urlEqualTo("/api/test"))
        .withHeader("Content-Type", equalTo("application/json"))
        .withRequestBody(equalToJson("{\"name\":\"John\"}")));
  }

  @Test
  public void testEmptyScan() throws Exception {
    // Stub a post request and provide a mocked response
    stubFor(post(urlEqualTo("/dp5/remote/v1/scan"))
        .withHeader("Content-Type", equalTo("application/json"))
        .willReturn(aResponse()
            .withStatus(201)
            .withHeader("Content-type", "application/json")
            .withBody("{\"scanId\":123,\"scanTime\":\"2024-10-07T19:20:17.413Z\"}")));

    // Define the URI using wiremock dynamic port
    URI uri = new URI("http://localhost:" + wiremockServer.port() + "/dp5/remote/v1/scan");

    // create an HttpClient and HttpRequest for the test
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .timeout(Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString(""))
        .build();

    // Send the request and get the response
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    //verify that the response is as expected
    assertEquals(201, response.statusCode());
    assertEquals("{\"scanId\":123,\"scanTime\":\"2024-10-07T19:20:17.413Z\"}", response.body());

    // verify that the POST request was made exactly once
    verify(postRequestedFor(urlEqualTo("/dp5/remote/v1/scan"))
        .withHeader("Content-Type", equalTo("application/json")));
  }

  @Test
  public void testErrorScan() throws Exception {
  }

  @Test
  public void testFullScan() throws Exception {
  }
}
