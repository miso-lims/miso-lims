package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class ExternalUriBuilder {
  private final Map<String, String> projectUris = new TreeMap<>();
  private final Map<PlatformType, Map<String, String>> runUris = new TreeMap<>();

  private static final String ID_PLACEHOLDER = "\\{id\\}";
  private static final String NAME_PLACEHOLDER = "\\{name\\}";
  private static final String ALIAS_PLACEHOLDER = "\\{alias\\}";
  private static final String CODE_PLACEHOLDER = "\\{code\\}";
  private static final String REPLACEHOLDER = "REPLACE";

  public Map<String, String> getUris(Project project) {
    if (!project.isSaved() || projectUris.isEmpty())
      return Collections.emptyMap();
    return projectUris.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, m -> expandProjectUrl(m.getValue(), project)));
  }

  public Map<String, String> getUris(Run run) {
    if (run.getId() == Run.UNSAVED_ID || runUris.isEmpty() || runUris.get(run.getPlatformType()) == null)
      return Collections.emptyMap();

    return runUris.get(run.getPlatformType())
        .entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, m -> expandRunUrl(m.getValue(), run)));
  }

  private String expandProjectUrl(String uriWithPlaceholders, Project project) {
    return uriWithPlaceholders.replaceAll(ID_PLACEHOLDER, String.valueOf(project.getId()))
        .replaceAll(NAME_PLACEHOLDER, project.getName())
        .replaceAll(CODE_PLACEHOLDER, project.getCode());
  }

  private String expandRunUrl(String uriWithPlaceholders, Run run) {
    return uriWithPlaceholders.replaceAll(ID_PLACEHOLDER, String.valueOf(run.getId()))
        .replaceAll(NAME_PLACEHOLDER, run.getName())
        .replaceAll(ALIAS_PLACEHOLDER, run.getAlias());
  }

  public void setProjectReportLinksConfig(String projectReportLinksConfigLine) {
    processProjectLinksConfig(projectReportLinksConfigLine, projectUris);
  }

  public void setRunReportLinksConfig(String runReportLinksConfigLine) {
    processRunLinksConfig(runReportLinksConfigLine, runUris);
  }

  public void processProjectLinksConfig(String linksConfigLine, Map<String, String> uriMap) {
    if (LimsUtils.isStringBlankOrNull(linksConfigLine))
      return;

    String[] configStrings = linksConfigLine.split("\\\\"); // multiple project report links can be
                                                            // double-backslash-separated (\\)
    for (int i = 0; i < configStrings.length; i++) {
      String[] configParts = configStrings[i].split("\\|"); // linksConfigLine format: <link text>|<URI with
                                                            // placeholders>
      validateConfigLength(configParts, 2);

      String linkText = configParts[0].trim();
      String uriWithPlaceholders = configParts[1].trim();
      validateUri(uriWithPlaceholders, Sets.newHashSet(ID_PLACEHOLDER, NAME_PLACEHOLDER, CODE_PLACEHOLDER));

      uriMap.put(linkText, uriWithPlaceholders);
    }
  }

  public void processRunLinksConfig(String linksConfigLine, Map<PlatformType, Map<String, String>> uriMap) {
    if (LimsUtils.isStringBlankOrNull(linksConfigLine))
      return;

    String[] configStrings = linksConfigLine.split("\\\\"); // multiple run report links can be
                                                            // double-backslash-separated (\\)
    for (int i = 0; i < configStrings.length; i++) {
      String[] configParts = configStrings[i].split("\\|"); // linksConfigLine format: <PlatformType,PlatformType>|<link
                                                            // text>|<URI with
                                                            // placeholders>
      validateConfigLength(configParts, 3);

      Set<String> platformTypeStrings = Sets.newHashSet(configParts[0].trim().split(","));
      Set<PlatformType> platformTypes =
          platformTypeStrings.stream().map(pt -> PlatformType.get(pt.trim())).collect(Collectors.toSet());
      if (platformTypes.isEmpty()) {
        throw new IllegalArgumentException(
            String.format("Invalid configuration: could not find any matching platforms for string %s",
                configParts[0]));
      }
      String linkText = configParts[1].trim();
      String uriWithPlaceholders = configParts[2].trim();
      validateUri(uriWithPlaceholders, Sets.newHashSet(ID_PLACEHOLDER, NAME_PLACEHOLDER, ALIAS_PLACEHOLDER));

      platformTypes.forEach(pt -> {
        if (uriMap.get(pt) == null) {
          Map<String, String> linkAndUri = new TreeMap<>();
          uriMap.put(pt, linkAndUri);
        }
        uriMap.get(pt).put(linkText, uriWithPlaceholders);
      });
    }
  }

  private void validateConfigLength(String[] configParts, Integer expectedLength) {
    if (configParts.length != expectedLength) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid configuration: expected %d link config parts separated by '|' for config string %s but got %d parts",
              expectedLength, String.join("|", configParts), configParts.length));
    }
  }

  private void validateUri(String uriWithPlaceholders, Set<String> possiblePlaceholders) {
    String validateableUri = uriWithPlaceholders;
    for (String placeholder : possiblePlaceholders) {
      validateableUri = validateableUri.replaceAll(placeholder, REPLACEHOLDER);
    }
    try {
      new URI(validateableUri);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(
          String.format("Invalid configuration: unable to parse valid URL from external link config %s",
              uriWithPlaceholders));
    }
  }
}
