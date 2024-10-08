package uk.ac.bbsrc.tgac.miso.core.data.qc;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
