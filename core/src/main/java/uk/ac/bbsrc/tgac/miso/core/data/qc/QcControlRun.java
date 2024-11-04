package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@MappedSuperclass
public abstract class QcControlRun implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long qcControlId = UNSAVED_ID;

  @ManyToOne
  @JoinColumn(name = "controlId")
  private QcControl control;

  private String lot;

  private boolean qcPassed;

  @Override
  public long getId() {
    return qcControlId;
  }

  @Override
  public void setId(long id) {
    this.qcControlId = id;
  }

  public QcControl getControl() {
    return control;
  }

  public void setControl(QcControl control) {
    this.control = control;
  }

  public String getLot() {
    return lot;
  }

  public void setLot(String lot) {
    this.lot = lot;
  }

  public boolean isQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public abstract QC getQc();

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        QcControlRun::getControl,
        QcControlRun::getLot,
        QcControlRun::isQcPassed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(control,
        lot,
        qcPassed);
  }

}
