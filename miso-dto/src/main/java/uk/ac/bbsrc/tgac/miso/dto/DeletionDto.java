package uk.ac.bbsrc.tgac.miso.dto;

public class DeletionDto {

  private long id;
  private String targetType;
  private long targetId;
  private String description;
  private String userName;
  private String changeTime;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTargetType() {
    return targetType;
  }

  public void setTargetType(String targetType) {
    this.targetType = targetType;
  }

  public long getTargetId() {
    return targetId;
  }

  public void setTargetId(long targetId) {
    this.targetId = targetId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(String changeTime) {
    this.changeTime = changeTime;
  }

}
