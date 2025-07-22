package uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate.mockServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.on.oicr.gsi.visionmate.RackType;
import ca.on.oicr.gsi.visionmate.Scan;
import ca.on.oicr.gsi.visionmate.ScanStatus;
import ca.on.oicr.gsi.visionmate.ServerConfig;

/**
 * Simple socket server to mock the behaviour of the VisionMate TCP/IP server for testing purposes.
 * Intended to run in a thread:
 * <p>
 * 
 * <pre>
 * {@code
 * MockScannerServer server = new MockScannerServer();
 * new Thread(server).start();
 * }
 * </pre>
 * <p>
 * When run, the server waits for a connection, will remain connected as long as the client stays,
 * and will shut down when the connection is closed by the client.
 * <p>
 * The mock server's state may be manipulated before issuing commands from a client.
 */
public class MockScannerServer implements Runnable {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private static final int defaultPort = 8000;

  private ServerConfig config = new ServerConfig();
  private final int port;

  private RackType currentProduct = new RackType("M0812");
  private ScanStatus currentStatus = new ScanStatus(33); // initialized, rack96
  private Scan currentData = null;

  /**
   * Constructs a new MockServerScanner that will listen on the default port (8000)
   */
  public MockScannerServer() {
    this.port = defaultPort;
  }

  /**
   * Constructs a new MockServerScanner that will listen on the specified port
   * 
   * @param port
   */
  public MockScannerServer(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    log.info("Mock server started");
    try (
        ServerSocket serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();

        PrintStream output = new PrintStream(clientSocket.getOutputStream());
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
      clientSocket.setSoTimeout(0);
      String request, response;

      while ((request = input.readLine()) != null) {
        try {
          response = handleRequest(request);
          output.print(response);
          output.flush();
        } catch (InternalServerException e) {
          // real server just doesn't respond to this.
          log.warn("No response due to internal server error", e);
        }
      }
      log.info("Mock server stopped");
    } catch (IOException e) {
      log.error("Mock server crashed due to I/O failure", e);
    }
  }

  /**
   * @return the current server configuration. Changes can be made to this object directly
   */
  public ServerConfig getConfig() {
    return config;
  }

  /**
   * Sets the server configuration. This must be matched identically by the client to ensure proper
   * function
   * 
   * @param config
   */
  public void setConfig(ServerConfig config) {
    this.config = config;
  }

  /**
   * @return the currently configured rack type
   */
  public RackType getCurrentProduct() {
    return currentProduct;
  }

  /**
   * Configures the current rack type. This must be set before emulating a scan
   * 
   * @param product
   */
  public void setCurrentProduct(RackType product) {
    this.currentProduct = product;
    resetStatus();
  }

  /**
   * Simulate a user scanning a rack containing the provided barcodes
   * 
   * @param barcodes
   */
  public void emulateScan(String[] barcodes) {
    if (barcodes.length != currentProduct.getRows() * currentProduct.getColumns()) {
      throw new IllegalArgumentException("Number of barcodes must match number of positions in current product");
    }
    StringBuilder sb = new StringBuilder();
    for (String barcode : barcodes) {
      sb.append(barcode).append(config.getDelimiter());
    }
    currentData = new Scan(currentProduct, sb.toString(), config);
    if (barcodes.length == 96) {
      currentStatus = new ScanStatus(45); // rack96, data ready, finished scan, initialized
    } else {
      currentStatus = new ScanStatus(13); // data ready, finished scan, initialized
    }
  }

  /**
   * Removes the current scan data and resets scan status
   */
  public void clearData() {
    currentData = null;
    resetStatus();
  }

  private String handleRequest(String request) throws InternalServerException {
    String response = processCommand(request);
    return decorateResponse(request, response);
  }

  /**
   * @param request the command issued
   * @return the appropriate response, an empty string if the command has no response, or null if the
   *         command is not valid
   * @throws InternalServerException if the server doesn't like something and decides to ignore it
   */
  private String processCommand(String request) throws InternalServerException {
    try {
      if (request.startsWith(config.getGetStatusCommand())) {
        return "" + currentStatus.toInt();
      } else if (request.startsWith(config.getGetProductCommand())) {
        return currentProduct.getStringRepresentation();
      } else if (request.startsWith(config.getSetProductCommand())) {
        String setting = request.replaceFirst(config.getSetProductCommand(), "");
        currentProduct = new RackType(setting);
        return "";
      } else if (request.startsWith(config.getResetStatusCommand())) {
        resetStatus();
        return "";
      } else if (request.startsWith(config.getGetDataCommand())) {
        // Real server sets status as if data was available and retrieved no matter what
        if (currentProduct.getRows() * currentProduct.getColumns() == 96) {
          currentStatus = new ScanStatus(61); // rack96, data sent, data ready, finished scan, initialized
        } else {
          currentStatus = new ScanStatus(29); // data sent, data ready, finished scan, initialized
        }
        if (currentData == null)
          throw new IllegalStateException("No data available"); // real server sends no response at all in this state
        else
          return getCurrentDataString();
      }
    } catch (IllegalStateException e) {
      throw new InternalServerException(e);
    } catch (IllegalArgumentException e) {
      throw new InternalServerException(e);
    }
    return null;
  }

  private void resetStatus() {
    if (currentProduct.getRows() * currentProduct.getColumns() == 96)
      currentStatus = new ScanStatus(33); // rack96, initialized
    else
      currentStatus = new ScanStatus(1); // initialized
  }

  private String getCurrentDataString() {
    StringBuilder sb = new StringBuilder();
    String[][] barcodes = currentData.getBarcodes();

    switch (config.getSortOrder()) {
      case ROWS:
        for (int row = 0; row < barcodes.length; row++) {
          for (int col = 0; col < barcodes[row].length; col++) {
            sb.append(barcodes[row][col]).append(config.getDelimiter());
          }
        }
        break;
      case COLUMNS:
        for (int col = 0; col < barcodes[0].length; col++) {
          for (int row = 0; row < barcodes.length; row++) {
            sb.append(barcodes[row][col]).append(config.getDelimiter());
          }
        }
        break;
    }

    return sb.toString();
  }

  /**
   * Formats the response data to conform to the configuration settings, and formatting of the real
   * server's output
   * 
   * @param request
   * @param response
   * @return
   */
  private String decorateResponse(String request, String response) {
    StringBuilder sb = new StringBuilder();
    if (config.getPrefixChar() != null)
      sb.append(config.getPrefixChar());
    sb.append(config.getAcknowledge());
    if (response == null) {
      sb.append("?");
      sb.append(request);
    } else {
      sb.append(request);
      sb.append(response);
    }
    if (config.getSuffixChar() != null)
      sb.append(config.getSuffixChar());
    return sb.toString();
  }

}
