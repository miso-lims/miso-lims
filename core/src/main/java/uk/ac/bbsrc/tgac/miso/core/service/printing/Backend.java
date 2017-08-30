package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.io.IOException;
import java.io.OutputStream;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.util.TransmissionUtils;

/**
 * Transfer method to send data to a printer.
 */
public enum Backend {
  CUPS() {
    @Override
    public boolean print(byte[] content, JsonNode configuration) {
      try {
        DocFlavor flavor = new DocFlavor("application/vnd.cups-raster", "[B");
        for (PrintService service : PrintServiceLookup.lookupPrintServices(flavor, new HashPrintRequestAttributeSet())) {
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
  FTP("host", "username", "password") {
    @Override
    public boolean print(byte[] content, JsonNode configuration) {
      String host = configuration.get("host").asText();
      String username = configuration.get("username").asText();
      String password = configuration.get("password").asText();

      try {
        FTPClient ftp = TransmissionUtils.ftpConnect(host, username, password);
        if (ftp == null || !ftp.isConnected()) {
          log.error("FTP client isn't connected. Please supply a client that has connected to the host.");
          return false;
        }

        if (!ftp.changeWorkingDirectory("/execute")) {
          log.error("Desired path does not exist on the server");
          ftp.logout();
          ftp.disconnect();
          return false;
        }
        try (OutputStream stream = ftp.storeFileStream(content.hashCode() + ".LBL")) {
          stream.write(content);
        }

        if (!ftp.completePendingCommand() || !FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
          log.error("Error storing file");
          ftp.logout();
          ftp.disconnect();
          return false;
        }
        ftp.logout();
        ftp.disconnect();
        return true;
      } catch (IOException e) {
        log.error("Failed to FTP", e);
      }
      return false;
    }
  },
  DEBUG() {
    @Override
    public boolean print(byte[] content, JsonNode configuration) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("Printing debug output:");
      for (byte b : content) {
        buffer.append(String.format(" %02x", b));
      }
      log.error(buffer.toString());
      return true;
    }
  };
  private static final Logger log = LoggerFactory.getLogger(Backend.class);

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
   * @return whether the printing was a success
   */
  public abstract boolean print(byte[] content, JsonNode configuration);

  /**
   * Send the supplied data to the printer
   * 
   * @param content The printer-specific data to be printed
   * @param configuration A string containing backend-specific configuration parameters to find the printer encoded as JSON.
   * @return
   */
  public boolean print(byte[] context, String configuration) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node;
    try {
      node = mapper.readTree(configuration);
      return print(context, node);
    } catch (IOException e) {
      log.error("Invalid printer configuration", e);
      return false;
    }
  }
}
