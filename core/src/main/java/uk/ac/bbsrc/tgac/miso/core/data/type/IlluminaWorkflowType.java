package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum IlluminaWorkflowType {

  NOVASEQ_STANDARD("NovaSeqStandard", "NovaSeq Standard"), NOVASEQ_XP("NovaSeqXp", "NovaSeq XP"), HISEQ_ONBOARD("OnBoardClustering", "HiSeq On-instrument"), HISEQ_CBOT("CBotTemplateHybridization", "HiSeq using cBot");

  private static final Map<String, IlluminaWorkflowType> lookup = new HashMap<>();

  static {
    for (IlluminaWorkflowType wf : EnumSet.allOf(IlluminaWorkflowType.class)) {
      lookup.put(wf.getRawValue(), wf);
    }
  }

  public static IlluminaWorkflowType get(String rawValue) {
    return lookup.get(rawValue);
  }

  private final String rawValue;
  private final String label;

  private IlluminaWorkflowType(String rawValue, String label) {
    this.rawValue = rawValue;
    this.label = label;
  }

  public String getRawValue() {
    return rawValue;
  }

  public String getLabel() {
    return label;
  }

}
