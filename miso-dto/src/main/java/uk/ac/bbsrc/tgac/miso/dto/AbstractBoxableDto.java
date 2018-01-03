package uk.ac.bbsrc.tgac.miso.dto;

public abstract class AbstractBoxableDto {

  private BoxDto box;
  private String boxPosition;
  private boolean discarded = false;

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

  public boolean isDiscarded() {
    return discarded;
  }

  public void setDiscarded(boolean discarded) {
    this.discarded = discarded;
  }

}
