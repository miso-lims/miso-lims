package uk.ac.bbsrc.tgac.miso.core.data.impl.workset;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@MappedSuperclass
public abstract class WorksetItem<T extends Boxable> implements Serializable {

  private static final long serialVersionUID = 1L;

  @Temporal(TemporalType.TIMESTAMP)
  private Date addedTime;

  public Date getAddedTime() {
    return addedTime;
  }

  public void setAddedTime(Date addedTime) {
    this.addedTime = addedTime;
  }

  public abstract Workset getWorkset();

  public abstract void setWorkset(Workset workset);

  public abstract T getItem();

  public abstract void setItem(T item);

  @Override
  public int hashCode() {
    return Objects.hash(addedTime, getItem());
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        WorksetItem::getAddedTime,
        WorksetItem::getItem);
  }

}
