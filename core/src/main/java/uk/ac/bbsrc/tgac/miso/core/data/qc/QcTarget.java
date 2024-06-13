package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum QcTarget {
  Library("Library", LibraryQC.class, QcCorrespondingField.NONE, QcCorrespondingField.CONCENTRATION,
      QcCorrespondingField.VOLUME, QcCorrespondingField.SIZE), //
  Sample("Sample", SampleQC.class, QcCorrespondingField.NONE, QcCorrespondingField.CONCENTRATION,
      QcCorrespondingField.VOLUME), //
  Pool("Pool", PoolQC.class, QcCorrespondingField.NONE, QcCorrespondingField.CONCENTRATION, QcCorrespondingField.VOLUME,
      QcCorrespondingField.SIZE), //
  Container("Container", ContainerQC.class, QcCorrespondingField.NONE), //
  Requisition("Requisition", RequisitionQC.class, QcCorrespondingField.NONE);

  private final String label;
  private final Class<? extends QC> entityClass;
  private final List<QcCorrespondingField> correspondingFields;

  public String getLabel() {
    return label;
  }

  public List<QcCorrespondingField> getCorrespondingFields() {
    return new ArrayList<>(correspondingFields);
  }

  public Class<? extends QC> getEntityClass() {
    return entityClass;
  }

  private QcTarget(String label, Class<? extends QC> entityClass, QcCorrespondingField... correspondingFields) {
    this.label = label;
    this.entityClass = entityClass;
    this.correspondingFields = Arrays.asList(correspondingFields);
  }
}
