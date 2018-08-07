package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class ExternalUriBuilder {
  private final Map<PlatformType, Map<String, String>> runUris = new TreeMap<>();

  public Map<String, String> getUris(Run run) {
    if (run.getId() == Run.UNSAVED_ID || runUris.isEmpty() || runUris.get(run.getPlatformType()) == null) return Collections.emptyMap();

    return runUris.get(run.getPlatformType())
        .entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, m -> expandRunUrl(m.getValue(), run)));
  }

  private String expandRunUrl(String uriWithPlaceholders, Run run) {
    return uriWithPlaceholders.replaceAll("\\{id\\}", String.valueOf(run.getId()))
        .replaceAll("\\{name\\}", run.getName())
        .replaceAll("\\{alias\\}", run.getAlias());
  }

  public void setRunReportLinksConfig(String runReportLinksConfigLine) {
    processLinksConfig(runReportLinksConfigLine, runUris);
  }

  public void processLinksConfig(String linksConfigLine, Map<PlatformType, Map<String, String>> uriMap) {
    if (LimsUtils.isStringBlankOrNull(linksConfigLine)) return;

    // linksConfigLine format: <PlatformType,PlatformType>|<link text>|<URI with placeholders>
    // placeholders can be any of {id}, {name}, {alias}.
    // multiple run report links can be double-backslash-separated (\\)
    String[] configStrings = linksConfigLine.split("\\\\");
    for (int i = 0; i < configStrings.length; i++) {
      String[] configParts = configStrings[i].split("\\|");
      if (configParts.length != 3) {
        throw new IllegalArgumentException(
            String.format("Invalid configuration: expected three link config parts separated by '|' for config string %s but got %d",
                configStrings[i], configParts.length));
      }

      Set<String> platformTypeStrings = Sets.newHashSet(configParts[0].trim().split(","));
      Set<PlatformType> platformTypes = platformTypeStrings.stream().map(pt -> PlatformType.get(pt.trim())).collect(Collectors.toSet());
      if (platformTypes.isEmpty()) {
        throw new IllegalArgumentException(
            String.format("Invalid configuration: could not find any matching platforms for string %s", configParts[0]));
      }
      String linkText = configParts[1].trim();
      String uriWithPlaceholders = configParts[2].trim();
      try {
        String validateableUri = uriWithPlaceholders.replaceAll("\\{name\\}", "REPLACE")
            .replaceAll("\\{id\\}", "REPLACE")
            .replaceAll("\\{alias\\}", "REPLACE");
        new URI(validateableUri);
      } catch (URISyntaxException e) {
        throw new IllegalArgumentException(
            String.format("Invalid configuration: unable to parse valid URL from external link config %s", configStrings[i]));
      }
      platformTypes.forEach(pt -> {
        if (uriMap.get(pt) == null) {
          Map<String, String> linkAndUri = new TreeMap<>();
          uriMap.put(pt, linkAndUri);
        }
        uriMap.get(pt).put(linkText, uriWithPlaceholders);
      });
    }
  }
}
