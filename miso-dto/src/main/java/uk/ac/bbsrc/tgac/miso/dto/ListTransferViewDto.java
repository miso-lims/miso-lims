package uk.ac.bbsrc.tgac.miso.dto;

public class ListTransferViewDto {

  private long id;
  private String transferTime;
  private Long senderLabId;
  private String senderLabLabel;
  private Long senderGroupId;
  private String senderGroupName;
  private String recipient;
  private Long recipientGroupId;
  private String recipientGroupName;
  private long items;
  private long received;
  private long receiptPending;
  private long qcPassed;
  private long qcPending;
  private String lastModified;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTransferTime() {
    return transferTime;
  }

  public void setTransferTime(String transferTime) {
    this.transferTime = transferTime;
  }

  public Long getSenderLabId() {
    return senderLabId;
  }

  public void setSenderLabId(Long senderLabId) {
    this.senderLabId = senderLabId;
  }

  public String getSenderLabLabel() {
    return senderLabLabel;
  }

  public void setSenderLabLabel(String senderLabLabel) {
    this.senderLabLabel = senderLabLabel;
  }

  public Long getSenderGroupId() {
    return senderGroupId;
  }

  public void setSenderGroupId(Long senderGroupId) {
    this.senderGroupId = senderGroupId;
  }

  public String getSenderGroupName() {
    return senderGroupName;
  }

  public void setSenderGroupName(String senderGroupName) {
    this.senderGroupName = senderGroupName;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public Long getRecipientGroupId() {
    return recipientGroupId;
  }

  public void setRecipientGroupId(Long recipientGroupId) {
    this.recipientGroupId = recipientGroupId;
  }

  public String getRecipientGroupName() {
    return recipientGroupName;
  }

  public void setRecipientGroupName(String recipientGroupName) {
    this.recipientGroupName = recipientGroupName;
  }

  public long getItems() {
    return items;
  }

  public void setItems(long items) {
    this.items = items;
  }

  public long getReceived() {
    return received;
  }

  public void setReceived(long received) {
    this.received = received;
  }

  public long getReceiptPending() {
    return receiptPending;
  }

  public void setReceiptPending(long receiptPending) {
    this.receiptPending = receiptPending;
  }

  public long getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(long qcPassed) {
    this.qcPassed = qcPassed;
  }

  public long getQcPending() {
    return qcPending;
  }

  public void setQcPending(long qcPending) {
    this.qcPending = qcPending;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

}
