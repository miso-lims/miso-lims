package uk.ac.bbsrc.tgac.miso.core.data.impl.view.box;

import java.io.Serializable;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BoxablePositionView implements Serializable {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "boxId")
  private BoxView box;

  private String position;

  public abstract long getId();

  public abstract void setId(long id);

  public BoxView getBox() {
    return box;
  }

  public void setBox(BoxView box) {
    this.box = box;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

}
