package uk.ac.bbsrc.tgac.miso.dto;

public abstract class AbstractBoxableDto {

  private BoxDto box;
  private String boxPosition;

  public BoxDto getBox() {
    return box;
  }

  public void setBox(BoxDto box) {
    this.box = box;
  }

  public String getBoxPosition() {
    return boxPosition;
  }

  public void setBoxPosition(String boxPosition) {
    this.boxPosition = boxPosition;
  }

}
