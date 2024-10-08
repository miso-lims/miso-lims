package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition.BoxPositionId;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;

@Entity
@IdClass(BoxPositionId.class)
public class BoxPosition implements Serializable {

  public static class BoxPositionId implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((box == null) ? 0 : box.hashCode());
      result = prime * result + ((position == null) ? 0 : position.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      BoxPositionId other = (BoxPositionId) obj;
      if (box == null) {
        if (other.box != null)
          return false;
      } else if (!box.equals(other.box))
        return false;
      if (position == null) {
        if (other.position != null)
          return false;
      } else if (!position.equals(other.position))
        return false;
      return true;
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne(targetEntity = BoxImpl.class)
  @JoinColumn(name = "boxId")
  private Box box;

  @Id
  private String position;

  @Embedded
  private BoxableId boxableId;

  public BoxPosition() {
    // default constructor
  }

  public BoxPosition(Box box, String position, BoxableId boxableId) {
    this.box = box;
    this.position = position;
    this.boxableId = boxableId;
  }

  public BoxPosition(Box box, String position, EntityType targetType, long targetId) {
    this(box, position, new BoxableId(targetType, targetId));
  }

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

  public BoxableId getBoxableId() {
    return boxableId;
  }

  public void setBoxableId(BoxableId boxableId) {
    this.boxableId = boxableId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((box == null) ? 0 : box.hashCode());
    result = prime * result + ((boxableId == null) ? 0 : boxableId.hashCode());
    result = prime * result + ((position == null) ? 0 : position.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BoxPosition other = (BoxPosition) obj;
    if (box == null) {
      if (other.box != null)
        return false;
    } else if (!box.equals(other.box))
      return false;
    if (boxableId == null) {
      if (other.boxableId != null)
        return false;
    } else if (!boxableId.equals(other.boxableId))
      return false;
    if (position == null) {
      if (other.position != null)
        return false;
    } else if (!position.equals(other.position))
      return false;
    return true;
  }

}
