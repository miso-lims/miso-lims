package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum DilutionFactor {
  
  TEN("1:10"), //
  HUNDRED("1:100"), //
  THOUSAND("1:1000"), //
  TEN_THOUSAND("1:10,000");
  
  private static final Map<String, DilutionFactor> lookup = new HashMap<>();

  static {
    for (DilutionFactor s : EnumSet.allOf(DilutionFactor.class))
      lookup.put(s.getLabel(), s);
  }

  public static DilutionFactor get(String label) {
    return lookup.get(label);
  }

  public static Set<String> getLabels() {
    return new HashSet<>(lookup.keySet());
  }

  private String label;
  
  private DilutionFactor(String label) {
    this.setLabel(label);
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
  
}
