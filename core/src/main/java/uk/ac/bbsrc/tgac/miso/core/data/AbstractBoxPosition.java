package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;

@MappedSuperclass
public abstract class AbstractBoxPosition implements Serializable {

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = BoxImpl.class)
  @JoinColumn(name = "boxId")
  private Box box;

  private String position;

  public Box getBox() {
    return box;
  }

  public void setBox(Box box) {
    this.box = box;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public abstract long getItemId();

  public abstract void setItemId(long id);

}
