package uk.ac.bbsrc.tgac.miso.core.data.impl.transfer;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
public class TransferNotification implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long notificationId = UNSAVED_ID;

  @ManyToOne
  @JoinColumn(name = "transferId")
  private Transfer transfer;

  private String recipientName;
  private String recipientEmail;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator")
  private User creator;

  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @Temporal(TemporalType.TIMESTAMP)
  private Date sentTime;

  private Boolean sendSuccess;

  @Temporal(TemporalType.TIMESTAMP)
  private Date failureSentTime;

  @Override
  public long getId() {
    return notificationId;
  }

  @Override
  public void setId(long id) {
    this.notificationId = id;
  }

  @Override
  public boolean isSaved() {
    return notificationId != UNSAVED_ID;
  }

  public Transfer getTransfer() {
    return transfer;
  }

  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
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

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getSentTime() {
    return sentTime;
  }

  public void setSentTime(Date sentTime) {
    this.sentTime = sentTime;
  }

  public Boolean getSendSuccess() {
    return sendSuccess;
  }

  public void setSendSuccess(Boolean sendSuccess) {
    this.sendSuccess = sendSuccess;
  }

  public Date getFailureSentTime() {
    return failureSentTime;
  }

  public void setFailureSentTime(Date failureSentTime) {
    this.failureSentTime = failureSentTime;
  }

}
