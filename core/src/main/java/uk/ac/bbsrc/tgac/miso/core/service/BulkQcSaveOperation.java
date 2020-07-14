package uk.ac.bbsrc.tgac.miso.core.service;

import java.util.List;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;

public class BulkQcSaveOperation extends BulkSaveOperation<QC> {

  private final QcTarget qcTarget;

  public BulkQcSaveOperation(QcTarget qcTarget, List<QC> pendingItems, User owner) {
    super(pendingItems, owner);
    this.qcTarget = qcTarget;
  }

  public QcTarget getQcTarget() {
    return qcTarget;
  }

}
