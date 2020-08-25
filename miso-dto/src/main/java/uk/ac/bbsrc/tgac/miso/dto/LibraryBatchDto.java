package uk.ac.bbsrc.tgac.miso.dto;

public class LibraryBatchDto {

  private String batchId;
  private String date;
  private long userId;
  private String username;
  private long sopId;
  private String sopLabel;
  private String sopUrl;
  private long kitId;
  private String kitName;
  private String kitLot;

  public String getBatchId() {
    return batchId;
  }

  public void setBatchId(String batchId) {
    this.batchId = batchId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public long getSopId() {
    return sopId;
  }

  public void setSopId(long sopId) {
    this.sopId = sopId;
  }

  public String getSopLabel() {
    return sopLabel;
  }

  public void setSopLabel(String sopLabel) {
    this.sopLabel = sopLabel;
  }

  public String getSopUrl() {
    return sopUrl;
  }

  public void setSopUrl(String sopUrl) {
    this.sopUrl = sopUrl;
  }

  public long getKitId() {
    return kitId;
  }

  public void setKitId(long kitId) {
    this.kitId = kitId;
  }

  public String getKitName() {
    return kitName;
  }

  public void setKitName(String kitName) {
    this.kitName = kitName;
  }

  public String getKitLot() {
    return kitLot;
  }

  public void setKitLot(String kitLot) {
    this.kitLot = kitLot;
  }

}
