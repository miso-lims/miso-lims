package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonBackReference;

import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;

@MappedSuperclass
public abstract class AbstractBoxPosition implements Serializable {

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = BoxImpl.class)
  @JoinColumn(name = "boxId")
  @JsonBackReference
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

}
