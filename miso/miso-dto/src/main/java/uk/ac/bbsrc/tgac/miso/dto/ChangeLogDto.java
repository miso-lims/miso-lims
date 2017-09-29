package uk.ac.bbsrc.tgac.miso.dto;

public class ChangeLogDto {
  private String summary;

  private String time;

  private String userName;

  public String getSummary() {
    return summary;
  }

  public String getTime() {
    return time;
  }

  public String getUserName() {
    return userName;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

}
