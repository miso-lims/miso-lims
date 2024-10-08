package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.util.TransmissionUtils;

/**
 * Transfer method to send data to a printer.
 */
public enum Backend {
  BRADY_FTP("host", "pin") {
    @Override
    public boolean print(byte[] content, JsonNode configuration, User user) {
      return sendFtpFile(configuration.get("host").asText(), "root", configuration.get("pin").asText(), "/execute",
          Arrays.hashCode(content) + ".LBL", content);
    }
  },
  CUPS() {
    @Override
    public boolean print(byte[] content, JsonNode configuration, User user) {
      try {
        DocFlavor flavor = new DocFlavor("application/vnd.cups-raster", "[B");
        for (PrintService service : PrintServiceLookup.lookupPrintServices(flavor,
            new HashPrintRequestAttributeSet())) {
          DocPrintJob job = service.createPrintJob();
          Doc doc = new SimpleDoc(content, flavor, null);
          job.print(doc, null);
          return true;
        }
        log.error("No printer found");
        return false;
      } catch (PrintException e) {
        log.error("print", e);
      }
      return false;
    }
  },
  DEBUG() {
    @SuppressWarnings({"squid:S2629", "squid:S1172"})
    @Override
    public boolean print(byte[] content, JsonNode configuration, User user) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("Printing debug output:");
      for (byte b : content) {
        buffer.append(String.format(" %02x", b));
      }
      log.error(buffer.toString());
      return true;
    }
  },
  LINE_PRINTER("host", "queue") {

    @SuppressWarnings({"squid:S3457"})
    @Override
    public boolean print(byte[] content, JsonNode configuration, User user) {
      String host = configuration.get("host").asText();
      String queue = configuration.get("queue").asText();
      // We need a three digit job id.
      int id = Arrays.hashCode(content);
      if (id < 0) {
        id = -id;
      }
      id = id % 1000;
      try (Socket socket = new Socket(host, 515);
          OutputStream output = socket.getOutputStream();
          InputStream input = socket.getInputStream()) {
        output.write(2);
        output.write(queue.getBytes(StandardCharsets.US_ASCII));
        output.write(10);
        output.flush();
        if (input.read() != 0) {
          return false;
        }
        byte[] control = String.format("Hmiso\nP%s\nvdfA%03dmiso\nN(gui)\nUdfA%03dmiso\n", user.getLoginName(), id, id)
            .getBytes(StandardCharsets.US_ASCII);
        output.write(String.format("\002%d cfA%03dmiso\n", control.length, id).getBytes(StandardCharsets.US_ASCII));
        output.flush();
        if (input.read() != 0) {
          return false;
        }
        output.write(control);
        output.write(0);
        if (input.read() != 0) {
          return false;
        }
        output.write(String.format("\003%d dfA%03dmiso\n", content.length, id).getBytes(StandardCharsets.US_ASCII));
        output.flush();
        if (input.read() != 0) {
          return false;
        }
        output.write(content);
        output.write(0);
        output.flush();
        if (input.read() != 0) {
          return false;
        }
      } catch (IOException e) {
        log.error("Failed to print to socket", e);
        return false;
      }
      return true;
    }

  },
  RAW_TCP("host", "port") {

    @Override
    public boolean print(byte[] content, JsonNode configuration, User user) {
      String host = configuration.get("host").asText();
      int port = configuration.get("port").asInt();
      try (Socket socket = new Socket(host, port)) {
        socket.getOutputStream().write(content);
      } catch (IOException e) {
        log.error("Failed to print to socket", e);
        return false;
      }
      return true;
    }

  },
  ZEBRA_FTP("host", "password") {
    @Override
    public boolean print(byte[] content, JsonNode configuration, User user) {
      return sendFtpFile(configuration.get("host").asText(), "admin", configuration.get("password").asText(),
          null, Arrays.hashCode(content) + ".ZPL", content);
    }

  };

  private static final Logger log = LoggerFactory.getLogger(Backend.class);

  protected static boolean sendFtpFile(String host, String username, String password, String directory, String filename,
      byte[] content) {

    try {
      FTPClient ftp = TransmissionUtils.ftpConnect(host, username, password);
      if (ftp == null || !ftp.isConnected()) {
        log.error("FTP client isn't connected. Please supply a client that has connected to the host.");
        return false;
      }

      try {
        if (directory == null) {// Do Nothing

        } else if (!ftp.changeWorkingDirectory(directory)) {
          log.error("Desired path does not exist on the server");
          return false;
        }
        try (OutputStream stream = ftp.storeFileStream(filename)) {
          stream.write(content);
        }

        if (!ftp.completePendingCommand() || !FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
          log.error("Error storing file");
          return false;
        }
        return true;
      } finally {
        ftp.logout();
        ftp.disconnect();
      }
    } catch (IOException e) {
      log.error("Failed to FTP", e);
    }
    return false;
  }

  private final List<String> configurationKeys;

  private Backend(String... keys) {
    configurationKeys = Collections.unmodifiableList(Arrays.asList(keys));
  }

  public List<String> getConfigurationKeys() {
    return configurationKeys;
  }

  /**
   * Send the supplied data to the printer
   * 
   * @param content The printer-specific data to be printed
   * @param configuration Backend-specific configuration parameters to find the printer.
   * @param user the user currently printing
   * @return whether the printing was a success
   */
  public abstract boolean print(byte[] content, JsonNode configuration, User user);

  /**
   * Send the supplied data to the printer
   * 
   * @param content The printer-specific data to be printed
   * @param configuration A string containing backend-specific configuration parameters to find the
   *        printer encoded as JSON.
   * @return
   */
  public boolean print(byte[] context, String configuration, User user) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node;
    try {
      node = mapper.readTree(configuration);
      return print(context, node, user);
    } catch (IOException e) {
      log.error("Invalid printer configuration", e);
      return false;
    }
  }
}
