package uk.ac.bbsrc.tgac.miso.dto;

public class TransferNotificationDto {

  private Long id;
  private String recipientName;
  private String recipientEmail;
  private String senderName;
  private String sentTime;
  private Boolean sendSuccess;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getRecipientName() {
    return recipientName;
  }

  public void setRecipientName(String recipientName) {
    this.recipientName = recipientName;
  }

  public String getRecipientEmail() {
    return recipientEmail;
  }

  public void setRecipientEmail(String recipientEmail) {
    this.recipientEmail = recipientEmail;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public String getSentTime() {
    return sentTime;
  }

  public void setSentTime(String sentTime) {
    this.sentTime = sentTime;
  }

  public Boolean getSendSuccess() {
    return sendSuccess;
  }

  public void setSendSuccess(Boolean sendSuccess) {
    this.sendSuccess = sendSuccess;
  }

}
