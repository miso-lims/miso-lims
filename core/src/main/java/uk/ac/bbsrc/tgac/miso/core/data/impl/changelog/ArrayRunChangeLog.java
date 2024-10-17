package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
