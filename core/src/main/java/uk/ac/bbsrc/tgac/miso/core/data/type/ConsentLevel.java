package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.HashMap;
import java.util.Map;

public enum ConsentLevel {

  ALL_PROJECTS("All Projects"),
  THIS_PROJECT("This Project"),
  REVOKED("Revoked");

  private static Map<String, ConsentLevel> byLabel;

  static {
    Map<String, ConsentLevel> map = new HashMap<>();
    for (ConsentLevel level : ConsentLevel.values()) {
      map.put(level.getLabel(), level);
    }
    byLabel = map;
  }

  public static ConsentLevel getByLabel(String label) {
    return byLabel.get(label);
  }

  private final String label;

  private ConsentLevel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

}
