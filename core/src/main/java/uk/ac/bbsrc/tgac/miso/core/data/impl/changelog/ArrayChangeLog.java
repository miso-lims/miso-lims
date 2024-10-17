package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Array;

@Entity
public class ArrayChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long arrayChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "arrayId", nullable = false, updatable = false)
  private Array array;

  @Override
  public Long getId() {
    return array.getId();
  }

  @Override
  public void setId(Long id) {
    array.setId(id);
  }

  public Long getArrayChangeLogId() {
    return arrayChangeLogId;
  }

  public Array getArray() {
    return array;
  }

  public void setArray(Array array) {
    this.array = array;
  }

}
