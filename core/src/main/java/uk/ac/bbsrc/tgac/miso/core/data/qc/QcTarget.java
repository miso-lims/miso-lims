package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum QcTarget {
  Library("Library", QcCorrespondingField.NONE, QcCorrespondingField.CONCENTRATION, QcCorrespondingField.VOLUME,
      QcCorrespondingField.SIZE), //
  Sample("Sample", QcCorrespondingField.NONE, QcCorrespondingField.CONCENTRATION, QcCorrespondingField.VOLUME), //
  Pool("Pool", QcCorrespondingField.NONE, QcCorrespondingField.CONCENTRATION, QcCorrespondingField.VOLUME,
      QcCorrespondingField.SIZE), //
  Run("Run", QcCorrespondingField.NONE), //
  Container("Container", QcCorrespondingField.NONE), //
  Requisition("Requisition", QcCorrespondingField.NONE);

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
