package uk.ac.bbsrc.tgac.miso.core.data.qc;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SampleQcControl")
public class SampleQcControlRun extends QcControlRun {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "qcId")
  private SampleQC qc;

  @Override
  public SampleQC getQc() {
    return qc;
  }

  public void setQc(SampleQC qc) {
    this.qc = qc;
  }

}
