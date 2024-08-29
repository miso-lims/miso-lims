package uk.ac.bbsrc.tgac.miso.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScanner;

@Configuration
public class BoxScannerConfigurer {

  protected static final Logger log = LoggerFactory.getLogger(BoxScannerConfigurer.class);

  @Value("${miso.boxscanner.servers:}")
  private String configLine;
  
  @Bean
  public Map<String, BoxScanner> boxScanners() {
    Map<String, BoxScanner> scanners = new HashMap<>();
    if (LimsUtils.isStringBlankOrNull(configLine)) {
      return scanners;
    }

    // config format: "<name>:<type>:<ip>:<port>" e.g. "Scanner Name:visionmate:127.0.0.1:9000"
    // multiple scanners may be comma-separated
    String[] configStrings = configLine.split(",");
    for (int i = 0; i < configStrings.length; i++) {
      String[] configParts = configStrings[i].split(":");
      if (configParts.length != 4
          || !Pattern.compile("^ *[a-zA-Z0-9][a-zA-Z0-9 ]{0,100} *$").matcher(configParts[0]).matches()
          || !(configParts[1].equals("visionmate") || configParts[1].equals("dp5mirage"))
          || !Pattern.compile("^ *[a-zA-Z0-9\\.]+ *$").matcher(configParts[2]).matches()
          || !Pattern.compile("^ *\\d{1,5} *$").matcher(configParts[3]).matches()) {
        throw new IllegalArgumentException("Invalid box scanner configuration: " + configStrings[i]);
      }
      String name = configParts[0].trim();
      String type = configParts[1].trim();
      String host = configParts[2].trim();
      int port = Integer.parseInt(configParts[3]);

      // Create correct scanner based on type
      if (type.equals("visionmate")) {
        VisionMateScanner scanner;
        try {
          scanner = new VisionMateScanner(host, port);
        } catch (IntegrationException e) {
          throw new IllegalStateException("Failed to initialize VisionMate scanner '" + name + "'", e);
        }
        scanners.put(name, scanner);
      }
      else if (type.equals("dp5mirage")) {
        DP5MirageScanner scanner;
        try {
          scanner = new DP5MirageScanner(host, port);
        } catch (IntegrationException e) {
          throw new IllegalStateException("Failed to initialize DP5Mirage scanner '" + name + "'", e);
        }
        scanners.put(name, scanner);
      }
    }

    return scanners;
  }

  @Bean
  public Boolean boxScannerEnabled() {
    return configLine != null;
  }

}
