package uk.ac.bbsrc.tgac.miso.integration.visionmate;

import java.io.IOException;
import java.net.UnknownHostException;

import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import ca.on.oicr.gsi.visionmate.RackType;
import ca.on.oicr.gsi.visionmate.RackType.Manufacturer;
import ca.on.oicr.gsi.visionmate.Scan;
import ca.on.oicr.gsi.visionmate.ScannerException;
import ca.on.oicr.gsi.visionmate.ServerConfig;
import ca.on.oicr.gsi.visionmate.VisionMateClient;

/**
 * This class integrates the VisionMate scanner client with MISO, and involves communicating with the scanner software via Telnet. 
 * The VisionMate server does not support multiple concurrent connections or sessions, so for each request issued, the response must 
 * be received before another request may be issued. Each operation in this class is therefor synchronized, and uses a new connection that 
 * is closed when the operation is completed
 * <p>
 * For proper usage, see {@link BoxScanner}
 */
public class VisionMateScanner implements BoxScanner {
  
  private static final int defaultCommunicationTimeout = 10000; // 10 sec
  private static final int maxCommunicationTimeout = 60000; // 1 min
  
  private static final int defaultScanTimeout = 15000; // 15 sec
  private static final int maxScanTimeout = 300000; // 5 min
  
  private final VisionMateClient client;
  private final int scanTimeout;
  
  /**
   * Constructs a new VisionMateScanner to communicate with a VisionMate server and retrieve scan data
   * 
   * @param host VisionMate server hostname or ip
   * @param port VisionMate server port
   * @param config VisionMate server configuration options
   * @param communicationTimeout time in milliseconds to wait for responses from the VisionMate server before timing out the request
   * @param scanTimeout time in milliseconds to wait before timing out when expecting a user to scan a box
   * @throws IntegrationException if the server hostname or ip could not be resolved
   */
  public VisionMateScanner(String host, int port, ServerConfig config, int communicationTimeout, int scanTimeout) throws IntegrationException {
    if (communicationTimeout < 1 || communicationTimeout > maxCommunicationTimeout) {
      throw new IllegalArgumentException("Communication timeout must be between 1 and " + maxCommunicationTimeout + " ms");
    }
    if (scanTimeout < 1 || scanTimeout > maxScanTimeout) {
      throw new IllegalArgumentException("Scan timeout must be between 1 and " + maxScanTimeout + " ms");
    }
    try {
      this.client = new VisionMateClient(host, port, config, communicationTimeout);
      this.scanTimeout = scanTimeout;
    } catch (UnknownHostException e) {
      throw new IntegrationException("Scanner host could not be resolved", e);
    }
  }
  
  /**
   * Constructs a new VisionMateScanner to communicate with a VisionMate server and retrieve scan data. The default communication and 
   * scan timeout values are used
   * 
   * @param host VisionMate server hostname or ip
   * @param port VisionMate server port
   * @param config VisionMate server configuration options
   * @throws IntegrationException if the server hostname or ip could not be resolved
   */
  public VisionMateScanner(String host, int port, ServerConfig config) throws IntegrationException {
    this(host, port, config, defaultCommunicationTimeout, defaultScanTimeout);
  }
  
  /**
   * Constructs a new VisionMateScanner to communicate with a VisionMate server and retrieve scan data. The default server configuration 
   * is used, which includes all default settings from VisionMate server software EXCEPT for suffix character, which is set to ASCII 
   * control character [3] (end of text character)
   * 
   * @param host VisionMate server hostname or ip
   * @param port VisionMate server port
   * @param communicationTimeout time in milliseconds to wait for responses from the VisionMate server before timing out the request
   * @param scanTimeout time in milliseconds to wait before timing out when expecting a user to scan a box
   * @throws IntegrationException if the server hostname or ip could not be resolved
   */
  public VisionMateScanner(String host, int port, int communicationTimeout, int scanTimeout) throws IntegrationException {
    this(host, port, new ServerConfig(), communicationTimeout, scanTimeout);
  }
  
  /**
   * Constructs a new VisionMateScanner to communicate with a VisionMate server and retrieve scan data. The default communication and 
   * scan timeout values are used. The default server configuration is used, which includes all default settings from VisionMate server 
   * software EXCEPT for suffix character, which is set to ASCII control character [3] (end of text character)
   * 
   * @param host VisionMate server hostname or ip
   * @param port VisionMate server port
   * @throws IntegrationException if the server hostname or ip could not be resolved
   */
  public VisionMateScanner(String host, int port) throws IntegrationException {
    this(host, port, new ServerConfig(), defaultCommunicationTimeout, defaultScanTimeout);
  }

  @Override
  public synchronized void prepareScan(int expectedRows, int expectedColumns) throws IntegrationException {
    try {
      client.connect();
      // Note: VisionMate documentation recommends setting Matrix as the manufacturer if the rack manufacturer is unknown. 
      // The row and column configuration is the important part of this.
      RackType rack = new RackType(Manufacturer.MATRIX, expectedRows, expectedColumns);
      
      if (!client.setCurrentProduct(rack) || !client.resetStatus()) {
        throw new IntegrationException("Scanner failed to execute preparation instructions");
      }
    } catch (IOException e) {
      throw new IntegrationException("Error communicating with the scanner", e);
    } finally {
      client.close();
    }
  }

  @Override
  public synchronized BoxScan getScan() throws IntegrationException {
    try {
      client.connect();
      Scan scan = client.waitForScan(scanTimeout);
      return scan == null ? null : new VisionMateScan(scan);
    } catch (IOException e) {
      throw new IntegrationException("Error communicating with the scanner", e);
    } catch (ScannerException e) {
      throw new IntegrationException("Error reported by the scanner", e);
    } finally {
      client.close();
    }
  }

}
