package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;

public class QcTargetDto {
  private QcTarget qcTarget;
  private List<QcCorrespondingField> correspondingFields;

  public QcTarget getQcTarget() {
    return qcTarget;
  }

  public void setQcTarget(QcTarget qcTarget) {
    this.qcTarget = qcTarget;
  }

  public List<QcCorrespondingField> getCorrespondingFields() {
    return correspondingFields;
  }

  public void setCorrespondingFields(List<QcCorrespondingField> correspondingFields) {
    this.correspondingFields = correspondingFields;
  }
}
