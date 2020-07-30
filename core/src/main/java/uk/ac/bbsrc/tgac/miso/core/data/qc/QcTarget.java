package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum QcTarget {
  Library("Library", QcCorrespondingField.NONE, QcCorrespondingField.CONCENTRATION, QcCorrespondingField.VOLUME), //
  Sample("Sample", QcCorrespondingField.NONE, QcCorrespondingField.CONCENTRATION, QcCorrespondingField.VOLUME), //
  Pool("Pool", QcCorrespondingField.NONE, QcCorrespondingField.CONCENTRATION, QcCorrespondingField.VOLUME), //
  Run("Run", QcCorrespondingField.NONE), //
  Container("Container", QcCorrespondingField.NONE);

  private final String label;
  private final List<QcCorrespondingField> correspondingFields;

  public String getLabel() {
    return label;
  }

  public List<QcCorrespondingField> getCorrespondingFields() {
    return new ArrayList<>(correspondingFields);
  }

  private QcTarget(String label, QcCorrespondingField... correspondingFields) {
    this.label = label;
    this.correspondingFields = Arrays.asList(correspondingFields);
  }
}
