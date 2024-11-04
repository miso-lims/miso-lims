package uk.ac.bbsrc.tgac.miso.core.data.qc;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ContainerQcControl")
public class ContainerQcControlRun extends QcControlRun {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "qcId")
  private ContainerQC qc;

  @Override
  public ContainerQC getQc() {
    return qc;
  }

  public void setQc(ContainerQC qc) {
    this.qc = qc;
  }

}
