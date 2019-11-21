package uk.ac.bbsrc.tgac.miso.core.data.qc;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "LibraryQcControl")
public class LibraryQcControlRun extends QcControlRun {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "qcId")
  private LibraryQC qc;

  @Override
  public LibraryQC getQc() {
    return qc;
  }

  public void setQc(LibraryQC qc) {
    this.qc = qc;
  }

}
