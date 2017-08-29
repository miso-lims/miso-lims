package uk.ac.bbsrc.tgac.miso.dto;

public class PartitionQCTypeDto {
  private String description;
  private long id;
  private boolean noteRequired;

  public String getDescription() {
    return description;
  }

  public long getId() {
    return id;
  }

  public boolean isNoteRequired() {
    return noteRequired;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setNoteRequired(boolean noteRequired) {
    this.noteRequired = noteRequired;
  }

}
