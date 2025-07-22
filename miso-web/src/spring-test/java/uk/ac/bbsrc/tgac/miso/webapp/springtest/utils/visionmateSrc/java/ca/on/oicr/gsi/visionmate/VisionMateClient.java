package uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.net.telnet.TelnetClient;

/**
 * This class is used to connect to a Thermo Scientific VisionMate server via Telnet and issue the
 * commands neccessary to retrieve a scan of barcodes.
 * <p>
 * The VisionMate server supports only one concurrent connection, and a second connection will
 * result in the disconnection of the first.
 * <p>
 * This class is not thread safe. External synchronization is required
 */
public class VisionMateClient implements AutoCloseable {

  private static final int defaultTimeout = 5000;

  /**
   * Default time to wait when expecting the user to scan a plate
   */
  private static final int defaultScanWait = 10000;

  /**
   * Time to wait between checks when waiting for a status change
   */
  private static final int statusPollingDelay = 100;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final InetAddress host;
  private final int port;
  private final TelnetClient telnet = new TelnetClient();
  private InputStream input;
  private PrintStream output;

  private final int timeout;
  private final ServerConfig serverConfig;

  /**
   * Constructs a new VisionMateClient using the default ServerConfig. This includes all default
   * VisionMate TCP/IP Server settings EXCEPT suffix character, which will default to "#" and must be
   * matched in the actual server configuration.
   * 
   * @param host hostname or IP address of VisionMate server
   * @param port port of VisionMate server
   * @throws UnknownHostException if the host is invalid or cannot be found
   */
  public VisionMateClient(String host, int port) throws UnknownHostException {
    this(host, port, new ServerConfig(), defaultTimeout);
  }

  /**
   * Constructs a new VisionMateClient using the provided ServerConfig. It is important to set a
   * suffix character in the server configuration, so that end of line can be detected.
   * 
   * @param host hostname or IP address of VisionMate server
   * @param port port of VisionMate server
   * @param config server configuration details
   * @param timeout milliseconds to wait before timing out a connection or request
   * @throws UnknownHostException if the host is invalid or cannot be found
   */
  public VisionMateClient(String host, int port, ServerConfig config, int timeout) throws UnknownHostException {
    if (config == null)
      throw new NullPointerException("server config cannot be null");

    this.host = InetAddress.getByName(host);
    this.port = port;
    this.serverConfig = config;
    this.timeout = timeout;
  }

  /**
   * Connects to the server. The configured timeout will be used both while connecting, and for
   * subsequent requests
   * 
   * @throws IOException if the client fails to connect
   */
  public void connect() throws IOException {
    telnet.connect(host, port);
    telnet.setSoTimeout(timeout);
    input = telnet.getInputStream();
    output = new PrintStream(telnet.getOutputStream());
    log.info("connected");
  }

  /**
   * @return true if the client is currently connected to the VisionMate server; false otherwise
   */
  public boolean isConnected() {
    return telnet.isConnected();
  }

  @Override
  public void close() {
    if (telnet.isConnected()) {
      try {
        telnet.disconnect();
      } catch (IOException e) {
        try {
          // retry once, then log failure
          telnet.disconnect();
        } catch (IOException e1) {
          log.error("Failed to close socket", e1);
        }
      }
      // Do not close streams. Disconnecting takes care of it
      input = null;
      output = null;
      log.info("disconnected");
    }
  }

  /**
   * Sends text to the server
   * 
   * @param command text to send
   */
  private void write(String command) {
    if (!telnet.isConnected())
      throw new IllegalStateException("Client is not connected.");
    output.print(command + "\r\n");
    output.flush();
    log.debug("sent: " + command);
  }

  /**
   * Waits for a response from the server and reads until the prompt (suffix character). This is a
   * blocking call and does not return until the response is received, or the socket timeout is
   * reached
   * 
   * @return the full response, including acknowledgement text, command echo, and response data
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read error occurs
   */
  private String readResponse() throws SocketTimeoutException, IOException {
    if (!telnet.isConnected())
      throw new IllegalStateException("Client is not connected.");
    StringBuilder sb = new StringBuilder();

    // Note: timeout is set on the socket, so reads may throw SocketTimeoutException
    char c = (char) input.read();
    while (c != serverConfig.getSuffixChar().charValue()) {
      sb.append(c);
      c = (char) input.read();
    }

    String response = sb.toString();
    log.debug("received: " + response);
    return response;
  }

  /**
   * Sends a command and waits for the response. This is a blocking call and does not return until the
   * response is received, or the socket timeout is reached
   * 
   * @param command text to send
   * @return the useful response data, which is the server's response, stripped of things such as
   *         prefix and suffix characters, acknowledgement text, and command echo
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   */
  private String sendCommand(String command) throws SocketTimeoutException, IOException {
    write(command);
    String response = readResponse();
    response.trim();
    response = response.replaceFirst(serverConfig.getResponseStartPattern(command), "");
    return response;
  }

  /**
   * Requests the scanner's status from the server. This is a blocking call and does not return until
   * the response is received, or the socket timeout is reached
   * 
   * @return a ScanStatus object representing the scanner's reported state
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   */
  public ScanStatus getStatus() throws SocketTimeoutException, IOException {
    String code = sendCommand(serverConfig.getGetStatusCommand());
    // cast and parse required because status is an unsigned byte, and Java bytes are signed
    return new ScanStatus(Integer.parseInt(code));
  }

  /**
   * Requests the current rack type information from the server. This is a blocking call and does not
   * return until the response is received, or the socket timeout is reached
   * 
   * @return a RackType object representing the current product
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   */
  public RackType getCurrentProduct() throws SocketTimeoutException, IOException {
    String code = sendCommand(serverConfig.getGetProductCommand());
    return new RackType(code);
  }

  /**
   * Sets the expected rack type information. This is a blocking call and does not return until the
   * response is received, or the socket timeout is reached
   * 
   * @param product the product to set
   * @return true if the product info is successfully set, retrieved, and verified; false otherwise
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   */
  public boolean setCurrentProduct(RackType product) throws SocketTimeoutException, IOException {
    sendCommand(serverConfig.getSetProductCommand(product));
    RackType result = getCurrentProduct();
    return result.equals(product);
  }

  /**
   * Resets the scanner's status. This marks its scanning, finished scan, data ready, data sent, and
   * error statuses all to false. Useful when intending to wait for a change (e.g. reset and wait for
   * data ready before retrieving scan to ensure that a new scan is being retrieved, and not a
   * previous one)
   * 
   * @return true if the status is successfully reset, retrieved, and verified; false otherwise
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   */
  public boolean resetStatus() throws SocketTimeoutException, IOException {
    sendCommand(serverConfig.getResetStatusCommand());

    // configured timeout will be used to choose how long to wait for status change verification. The
    // resulting timeout will be this,
    // plus the time it takes for each call. An individual call is more likely to time out than the
    // status change though.
    for (int i = timeout / statusPollingDelay; i > 0; i--) {
      try {
        Thread.sleep(statusPollingDelay);
      } catch (InterruptedException e) {
        log.error("Sleeping thread interrupted", e);
      }
      ScanStatus status = getStatus();
      if (!status.isDataReady())
        return true;
    }
    return false;
  }

  /**
   * Retrieves the most recent scan results from the server. Caution: old results are saved
   * indefinitely. If you want to ensure that you are retrieving a new scan, it is best to call
   * {@link #resetStatus()} and then wait until new data is ready before calling this method.
   * Alternately, see the {@link #waitForScan()} and {@link #prepareAndWaitForScan()} methods
   * 
   * @return the scan data, including all rack position data, or null if there is no available scan
   *         data
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   * @throws ScannerException if the hardware scanner reports an error
   */
  public Scan getScan() throws SocketTimeoutException, IOException, ScannerException {
    ScanStatus status = getStatus();
    if (status.isError())
      throw new ScannerException(getLastError());
    if (!status.isDataReady())
      return null;

    RackType product = getCurrentProduct();
    String scan = null;
    try {
      scan = sendCommand(serverConfig.getGetDataCommand());
    } catch (SocketTimeoutException e) {
      // If there is no data, the server sends no response at all
      // Sending CR LF should get an empty response if this is the case
      scan = sendCommand("");
    }

    if (scan.isEmpty())
      return null;
    return new Scan(product, scan, serverConfig);
  }

  public String getLastError() throws SocketTimeoutException, IOException {
    return sendCommand(serverConfig.getGetLastErrorCommand());
  }

  /**
   * Waits for the server to report data ready, then retrieves scan results from the server. It is
   * ideal to call {@link #resetStatus()} before this method to ensure that old data is not mistakenly
   * retrieved. Alternately, see the {@link #prepareAndWaitForScan()} method. This is a blocking call
   * and does not return until the status has changed and scan results have been retrieved, or the
   * maxWaitTime has elapsed
   * 
   * @param maxWaitTime milliseconds to wait for data to be ready
   * @return the scan data, including all rack position data, or null if there is no available scan
   *         data
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   * @throws ScannerException if the hardware scanner reports an error
   */
  public Scan waitForScan(int maxWaitTime) throws SocketTimeoutException, IOException, ScannerException {
    for (int i = maxWaitTime / statusPollingDelay; i > 0; i--) {
      ScanStatus status = getStatus();
      if (status.isDataReady()) {
        return getScan();
      }
      try {
        Thread.sleep(statusPollingDelay);
      } catch (InterruptedException e) {
        log.error("Sleeping thread interrupted", e);
      }
    }
    return null;
  }

  /**
   * Waits for the server to report data ready, then retrieves scan results from the server. It is
   * ideal to call {@link #resetStatus()} before this method to ensure that old data is not mistakenly
   * retrieved. Alternately, see the {@link #prepareAndWaitForScan()} method. This is a blocking call
   * and does not return until the status has changed and scan results have been retrieved, or the
   * default wait time has elapsed
   * 
   * @return the scan data, including all rack position data, or null if there is no available scan
   *         data
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   * @throws ScannerException if the hardware scanner reports an error
   */
  public Scan waitForScan() throws SocketTimeoutException, IOException, ScannerException {
    return waitForScan(defaultScanWait);
  }

  /**
   * Resets the scanner status, waits for the server to report data ready, then retrieves scan results
   * from the server. This ensures that data retrieved is from a scan that runs while this method is
   * running. This is a blocking call and does not return until the status has changed and scan
   * results have been retrieved, or the maxWaitTime has elapsed
   * 
   * @param maxWaitTime milliseconds to wait for data to be ready
   * @return the scan data, including all rack position data, or null if there is no available scan
   *         data
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   * @throws ScannerException if the hardware scanner reports an error
   */
  public Scan prepareAndWaitForScan(int maxWaitTime) throws SocketTimeoutException, IOException, ScannerException {
    if (!resetStatus()) {
      throw new IOException("Unable to reset scanner state");
    }

    return waitForScan(maxWaitTime);
  }

  /**
   * Resets the scanner status, waits for the server to report data ready, then retrieves scan results
   * from the server. This ensures that data retrieved is from a scan that runs while this method is
   * running. This is a blocking call and does not return until the status has changed and scan
   * results have been retrieved, or the default wait time has elapsed
   * 
   * @return the scan data, including all rack position data, or null if there is no available scan
   *         data
   * @throws SocketTimeoutException if wait for response times out
   * @throws IOException if any other read/write errors occur in communication
   * @throws ScannerException if the hardware scanner reports an error
   */
  public Scan prepareAndWaitForScan() throws SocketTimeoutException, IOException, ScannerException {
    return prepareAndWaitForScan(defaultScanWait);
  }

}
