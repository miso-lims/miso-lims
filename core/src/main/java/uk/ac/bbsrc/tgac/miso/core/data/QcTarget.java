package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum QcTarget {
  Library(QcCorrespondingField.NONE), Sample(QcCorrespondingField.NONE), Pool(QcCorrespondingField.NONE), Run(QcCorrespondingField.NONE), Container(QcCorrespondingField.NONE);

  private final List<QcCorrespondingField> correspondingFields;

  public List<QcCorrespondingField> getCorrespondingFields() {
    return new ArrayList<>(correspondingFields);
  }

  private QcTarget(QcCorrespondingField... correspondingFields) {
    this.correspondingFields = Arrays.asList(correspondingFields);
  }
}
