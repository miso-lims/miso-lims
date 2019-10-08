package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.HashMap;
import java.util.Map;

public enum InstrumentDataManglingPolicy {
  I5_RC("Reverse compliment i5"), // An Illumina sequencer which sequences the reverse complement of the reverse primer, rather than the
                                  // primer
  NONE("Normal");

  private static final Map<String, InstrumentDataManglingPolicy> lookup = new HashMap<>();

  static {
    for (InstrumentDataManglingPolicy item : InstrumentDataManglingPolicy.values()) {
      lookup.put(item.getLabel(), item);
    }
  }

  public static InstrumentDataManglingPolicy get(String label) {
    return lookup.get(label);
  }

  private final String label;

  private InstrumentDataManglingPolicy(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
