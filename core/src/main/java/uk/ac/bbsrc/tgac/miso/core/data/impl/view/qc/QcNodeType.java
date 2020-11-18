package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.util.HashMap;
import java.util.Map;

public enum QcNodeType {

  SAMPLE("Sample"), //
  LIBRARY("Library"), //
  LIBRARY_ALIQUOT("Library Aliquot"), //
  POOL("Pool"), //
  RUN("Run"), //
  RUN_PARTITION("Run-Partition"), //
  RUN_LIBRARY("Run-Library");

  private static final Map<String, QcNodeType> map;

  static {
    map = new HashMap<>();
    for (QcNodeType value : QcNodeType.values()) {
      map.put(value.getLabel(), value);
    }
  }

  private final String label;

  private QcNodeType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static QcNodeType lookup(String label) {
    return map.get(label);
  }

}
