package uk.ac.bbsrc.tgac.miso.core.data.qc;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PoolQcControl")
public class PoolQcControlRun extends QcControlRun {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "qcId")
  private PoolQC qc;

  @Override
  public PoolQC getQc() {
    return qc;
  }

  public void setQc(PoolQC qc) {
    this.qc = qc;
  }

}
