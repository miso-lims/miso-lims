package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Possible status options for Short Tandem Repeat QC
 */
public enum StrStatus {

  NOT_SUBMITTED("Not Submitted"), SUBMITTED("Submitted"), PASS("Pass"), FAIL("Fail");

  private static final Map<String, StrStatus> lookup = new HashMap<>();

  static {
    for (StrStatus sr : StrStatus.values()) {
      lookup.put(sr.getLabel(), sr);
    }
  }

  private final String label;

  private StrStatus(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  /**
   * Finds a StrStatus value by its label
   * 
   * @throws IllegalArgumentException
   *           if no StrStatus with the requested label exists
   */
  public static StrStatus get(String label) {
    if (!lookup.containsKey(label)) throw new IllegalArgumentException("Invalid STR Status: " + label);
    return lookup.get(label);
  }

  public static List<String> getLabels() {
    return new ArrayList<String>(lookup.keySet());
  }

}