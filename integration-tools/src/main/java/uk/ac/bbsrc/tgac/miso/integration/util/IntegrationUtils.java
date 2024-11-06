package uk.ac.bbsrc.tgac.miso.integration.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uk.ac.bbsrc.tgac.miso.integration.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 04/11/11
 * @since 0.1.3
 */
public class IntegrationUtils {
  private static final Logger log = LoggerFactory.getLogger(IntegrationUtils.class);

  /**
   * Sets up the socket connection to a given host
   * 
   * @param host of type String
   * @param port of type int
   * @return Socket
   * @throws IntegrationException when the socket couldn't be created
   */
  public static Socket prepareSocket(String host, int port) throws IntegrationException {
    try {
      return new Socket(host, port);
    } catch (IOException e) {
      log.error("prepare socket", e);
      throw new IntegrationException("Cannot connect to " + host + ":" + port + ". Cause: " + e.getMessage());
    }
  }

  /**
   * Sends a String message to a given host socket
   * 
   * @param socket of type Socket
   * @param query of type String
   * @return String
   * @throws IntegrationException when the socket couldn't be created
   */
  public static String sendMessage(Socket socket, String query) throws IntegrationException {
    BufferedWriter wr = null;
    BufferedReader rd = null;
    try {
      wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));

      // Send data
      wr.write(query + "\r\n");
      wr.flush();

      // Get response
      rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = rd.readLine()) != null) {
        sb.append(line);
      }
      wr.close();
      rd.close();

      String dirty = sb.toString();
      StringBuilder response = new StringBuilder();
      int codePoint;
      int i = 0;
      while (i < dirty.length()) {
        codePoint = dirty.codePointAt(i);
        if ((codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
            || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
            || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
            || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
          response.append(Character.toChars(codePoint));
        }
        i += Character.charCount(codePoint);
      }

      return response.toString().replace("\\\n", "").replace("\\\t", "");
    } catch (UnknownHostException e) {
      log.error("Cannot resolve host: " + socket.getInetAddress(), e);
      throw new IntegrationException(e.getMessage());
    } catch (IOException e) {
      log.error("Couldn't get I/O for the connection to: " + socket.getInetAddress(), e);
      throw new IntegrationException(e.getMessage());
    } finally {
      try {
        if (wr != null) {
          wr.close();
        }
        if (rd != null) {
          rd.close();
        }
      } catch (Throwable t) {
        log.error("close socket", t);
      }
    }
  }

  public static byte[] compress(byte[] content) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Base64OutputStream b64os = new Base64OutputStream(baos);
    GZIPOutputStream gzip = new GZIPOutputStream(b64os);
    gzip.write(content);
    gzip.close();
    return baos.toByteArray();
  }

  public static byte[] decompress(byte[] contentBytes) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    GZIPInputStream bis = new GZIPInputStream(new Base64InputStream(new ByteArrayInputStream(contentBytes)));
    byte[] buffer = new byte[1024 * 4];
    int n = 0;
    while (-1 != (n = bis.read(buffer))) {
      out.write(buffer, 0, n);
    }
    bis.close();
    return out.toByteArray();
  }

  private static String getParameterAppend(Map<String,String> parameters) {
    return parameters.entrySet().stream().map(e -> new StringBuilder(e.getKey()).append("=").append(e.getValue()).toString()).collect(
        Collectors.joining("&"));
  }

  /**
   * Sending a POST request
   * @param httpClient
   * @param uri
   * @param parameters
   * @return
   * @throws URISyntaxException
   */
  public static HttpResponse<String> GetPostParamRequest(HttpClient httpClient, URI uri,
      Map<String, String> parameters)
      throws URISyntaxException, IOException, InterruptedException {
    int postActionTimeout = 10;
    if(parameters != null) {
      String queryParam = IntegrationUtils.getParameterAppend(parameters);
      uri= new URI(uri.getScheme(), uri.getAuthority(),
          uri.getPath(), queryParam, uri.getFragment());
    }

    return httpClient.send(HttpRequest.newBuilder().uri(uri)
        .timeout(Duration.ofSeconds(postActionTimeout))
        .POST(BodyPublishers.ofString("")).build(), BodyHandlers.ofString());
  }
}
