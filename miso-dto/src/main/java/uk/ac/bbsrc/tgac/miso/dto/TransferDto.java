package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class TransferDto {

  private Long id;
  private String transferRequestName;
  private String transferTime;
  private Long senderLabId;
  private String senderLabLabel;
  private Long senderGroupId;
  private String senderGroupName;
  private String recipient;
  private Long recipientGroupId;
  private String recipientGroupName;
  private List<TransferItemDto> items;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTransferRequestName() {
    return transferRequestName;
  }

  public void setTransferRequestName(String transferRequestName) {
    this.transferRequestName = transferRequestName;
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

  public List<TransferItemDto> getItems() {
    return items;
  }

  public void setItems(List<TransferItemDto> items) {
    this.items = items;
  }

}
