package uk.ac.bbsrc.tgac.miso.core.data.qc;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
