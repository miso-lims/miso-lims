package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;

public class LibraryTemplateIndexDto {

  private String boxPosition;
  private Long index1Id;
  private Long index2Id;

  public LibraryTemplateIndexDto() {
    // Default constructor
  }

  public LibraryTemplateIndexDto(String boxPosition, LibraryIndex index1, LibraryIndex index2) {
    this.boxPosition = boxPosition;
    if (index1 != null) {
      this.index1Id = index1.getId();
    }
    if (index2 != null) {
      this.index2Id = index2.getId();
    }
  }

  public String getBoxPosition() {
    return boxPosition;
  }

  public void setBoxPosition(String boxPosition) {
    this.boxPosition = boxPosition;
  }

  public Long getIndex1Id() {
    return index1Id;
  }

  public void setIndex1Id(Long index1Id) {
    this.index1Id = index1Id;
  }

  public Long getIndex2Id() {
    return index2Id;
  }

  public void setIndex2Id(Long index2Id) {
    this.index2Id = index2Id;
  }

}
