package uk.ac.bbsrc.tgac.miso.core.data.qc;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "RequisitionQcControl")
public class RequisitionQcControlRun extends QcControlRun {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "qcId")
  private RequisitionQC qc;

  @Override
  public RequisitionQC getQc() {
    return qc;
  }

  public void setQc(RequisitionQC qc) {
    this.qc = qc;
  }

}
