package uk.ac.bbsrc.tgac.miso.core.data.qc;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
