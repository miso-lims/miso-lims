package uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * CLI app mainly for testing with a real scanner
 */
public class CliClient {

  private static final String cmdStatus = "status";
  private static final String cmdReset = "reset";
  private static final String cmdGetScan = "data";
  private static final String cmdAwaitScan = "waitscan";
  private static final String cmdPrepAndAwaitScan = "prepscan";
  private static final String cmdExit = "exit";
  private static final String cmdHelp = "help";

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage: java -jar <jarfile> <server-ip> <server-port>");
      return;
    }
    try (
        VisionMateClient scanner = new VisionMateClient(args[0], Integer.parseInt(args[1]), new ServerConfig(), 0);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
      scanner.connect();
      displayHelp();
      while (true) {
        String input = stdIn.readLine();
        if (input == null || cmdExit.equals(input))
          break;
        handleInput(scanner, input);
      }
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return;
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }

  private static void handleInput(VisionMateClient scanner, String input) throws SocketTimeoutException, IOException {
    try {
      if (cmdHelp.equals(input)) {
        displayHelp();
      } else if (cmdStatus.equals(input)) {
        ScanStatus status = scanner.getStatus();
        System.out.println(status.toString());
      } else if (cmdReset.equals(input)) {
        scanner.resetStatus();
        ScanStatus status = scanner.getStatus();
        System.out.println(status.toString());
      } else if (cmdGetScan.equals(input)) {
        try {
          Scan scan = scanner.getScan();
          System.out.println(makeBarcodeString(scan));
        } catch (ScannerException e) {
          System.out.println("Scanner error: " + e.getMessage());
        }
      } else if (cmdAwaitScan.equals(input)) {
        Scan scan = scanner.waitForScan(10000);
        System.out.println(makeBarcodeString(scan));
      } else if (cmdPrepAndAwaitScan.equals(input)) {
        Scan scan = scanner.prepareAndWaitForScan(10000);
        System.out.println(makeBarcodeString(scan));
      }
    } catch (ScannerException e) {
      System.out.println("Scanner error: " + e.getMessage());
    }
  }

  private static String makeBarcodeString(Scan scan) {
    if (scan == null)
      return "No scan data available";
    String[][] barcodes = scan.getBarcodes();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < barcodes.length; i++) {
      for (int j = 0; j < barcodes[0].length; j++) {
        sb.append(barcodes[i][j]).append(",");
      }
    }
    return sb.toString();
  }

  private static void displayHelp() {
    System.out.println("VisionMate Client CLI. Available commands:");
    System.out.println("\t" + cmdStatus + "\t\tGet scanner status");
    System.out.println("\t" + cmdReset + "\t\tReset scanner status");
    System.out.println("\t" + cmdGetScan + "\t\tGet scan data");
    System.out.println("\t" + cmdAwaitScan + "\tWait for scan");
    System.out.println("\t" + cmdPrepAndAwaitScan + "\tReset status and wait for scan");
    System.out.println("\t" + cmdExit + "\t\tClose this program");
    System.out.println("\t" + cmdHelp + "\t\tDisplay this message");
  }

}
