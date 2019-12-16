package uk.ac.bbsrc.tgac.miso.dto;

public class ListTransferViewDto {

  private long id;
  private String transferDate;
  private Long senderLabId;
  private String senderLabLabel;
  private Long senderGroupId;
  private String senderGroupName;
  private String recipient;
  private Long recipientGroupId;
  private String recipientGroupName;
  private int items;
  private int received;
  private int receiptPending;
  private int qcPassed;
  private int qcPending;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTransferDate() {
    return transferDate;
  }

  public void setTransferDate(String transferDate) {
    this.transferDate = transferDate;
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

  public int getItems() {
    return items;
  }

  public void setItems(int items) {
    this.items = items;
  }

  public int getReceived() {
    return received;
  }

  public void setReceived(int received) {
    this.received = received;
  }

  public int getReceiptPending() {
    return receiptPending;
  }

  public void setReceiptPending(int receiptPending) {
    this.receiptPending = receiptPending;
  }

  public int getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(int qcPassed) {
    this.qcPassed = qcPassed;
  }

  public int getQcPending() {
    return qcPending;
  }

  public void setQcPending(int qcPending) {
    this.qcPending = qcPending;
  }

}
