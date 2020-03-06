package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.HashMap;
import java.util.Map;

public enum InstrumentType {

  // Note: ListInstrumentsController sorts InstrumentType tabs by ordinal
  SEQUENCER("Sequencer"),
  ARRAY_SCANNER("Array Scanner"),
  THERMAL_CYCLER("Thermal Cycler"),
  OTHER("Other");

  private static final Map<String, InstrumentType> lookup;

  static {
    Map<String, InstrumentType> map = new HashMap<>();
    for (InstrumentType it : InstrumentType.values()) {
      map.put(it.getLabel(), it);
    }
    lookup = map;
  }

  public static InstrumentType get(String label) {
    return lookup.get(label);
  }

  private final String label;

  private InstrumentType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

}
