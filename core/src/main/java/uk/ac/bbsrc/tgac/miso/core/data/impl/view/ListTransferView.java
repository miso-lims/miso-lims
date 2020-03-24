package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;

@Entity
@Immutable
public class ListTransferView implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long transferId;

  @Temporal(TemporalType.DATE)
  private Date transferTime;

  @ManyToOne(targetEntity = LabImpl.class)
  @JoinColumn(name = "senderLabId")
  private Lab senderLab;

  @ManyToOne(targetEntity = Group.class)
  @JoinColumn(name = "senderGroupId")
  private Group senderGroup;

  private String recipient;

  @ManyToOne(targetEntity = Group.class)
  @JoinColumn(name = "recipientGroupId")
  private Group recipientGroup;

  private int items;
  private int received;
  private int receiptPending;
  private int qcPassed;
  private int qcPending;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @Override
  public long getId() {
    return transferId;
  }

  @Override
  public void setId(long id) {
    this.transferId = id;
  }

  public Date getTransferTime() {
    return transferTime;
  }

  public void setTransferTime(Date transferTime) {
    this.transferTime = transferTime;
  }

  public Lab getSenderLab() {
    return senderLab;
  }

  public void setSenderLab(Lab senderLab) {
    this.senderLab = senderLab;
  }

  public Group getSenderGroup() {
    return senderGroup;
  }

  public void setSenderGroup(Group senderGroup) {
    this.senderGroup = senderGroup;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public Group getRecipientGroup() {
    return recipientGroup;
  }

  public void setRecipientGroup(Group recipientGroup) {
    this.recipientGroup = recipientGroup;
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

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public boolean isSaved() {
    return true;
  }

  public boolean isReceipt() {
    return getSenderLab() != null;
  }

  public boolean isDistribution() {
    return getRecipient() != null;
  }

}
