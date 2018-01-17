package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;

@Entity
public class ArrayRunChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long arrayRunChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "arrayRunId", nullable = false, updatable = false)
  private ArrayRun arrayRun;

  @Override
  public Long getId() {
    return arrayRun.getId();
  }

  @Override
  public void setId(Long id) {
    arrayRun.setId(id);
  }

  public Long getArrayChangeLogId() {
    return arrayRunChangeLogId;
  }

  public ArrayRun getArrayRun() {
    return arrayRun;
  }

  public void setArrayRun(ArrayRun arrayRun) {
    this.arrayRun = arrayRun;
  }

}
