package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class QcControl implements Aliasable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long controlId = UNSAVED_ID;

  @ManyToOne
  @JoinColumn(name = "qcTypeId")
  private QcType qcType;

  private String alias;

  @Override
  public long getId() {
    return controlId;
  }

  @Override
  public void setId(long id) {
    this.controlId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public QcType getQcType() {
    return qcType;
  }

  public void setQcType(QcType qcType) {
    this.qcType = qcType;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        QcControl::getQcType,
        QcControl::getAlias);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qcType, alias);
  }

}
