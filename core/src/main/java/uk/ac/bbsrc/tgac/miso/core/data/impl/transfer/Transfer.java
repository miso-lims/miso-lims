package uk.ac.bbsrc.tgac.miso.core.data.impl.transfer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.TransferChangeLog;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Transfer implements ChangeLoggable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long transferId;

  private String transferRequestName;

  @Temporal(TemporalType.TIMESTAMP)
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

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator")
  private User creator;

  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @OneToMany(targetEntity = TransferChangeLog.class, mappedBy = "transfer", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL)
  private Set<TransferSample> sampleTransfers;

  @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL)
  private Set<TransferLibrary> libraryTransfers;

  @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL)
  private Set<TransferLibraryAliquot> libraryAliquotTransfers;

  @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL)
  private Set<TransferPool> poolTransfers;

  @Override
  public long getId() {
    return transferId;
  }

  @Override
  public void setId(long id) {
    this.transferId = id;
  }

  public String getTransferRequestName() {
    return transferRequestName;
  }

  public void setTransferRequestName(String transferRequestName) {
    this.transferRequestName = transferRequestName;
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

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User user) {
    this.creator = user;
  }

  @Override
  public Date getCreationTime() {
    return created;
  }

  @Override
  public void setCreationTime(Date creationTime) {
    this.created = creationTime;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User user) {
    this.lastModifier = user;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    TransferChangeLog change = new TransferChangeLog();
    change.setTransfer(this);
    change.setSummary(summary);
    change.setColumnsChanged(columnsChanged);
    change.setUser(user);
    return change;
  }

  public Set<TransferSample> getSampleTransfers() {
    if (sampleTransfers == null) {
      sampleTransfers = new HashSet<>();
    }
    return sampleTransfers;
  }

  public Set<TransferLibrary> getLibraryTransfers() {
    if (libraryTransfers == null) {
      libraryTransfers = new HashSet<>();
    }
    return libraryTransfers;
  }

  public Set<TransferLibraryAliquot> getLibraryAliquotTransfers() {
    if (libraryAliquotTransfers == null) {
      libraryAliquotTransfers = new HashSet<>();
    }
    return libraryAliquotTransfers;
  }

  public Set<TransferPool> getPoolTransfers() {
    if (poolTransfers == null) {
      poolTransfers = new HashSet<>();
    }
    return poolTransfers;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public boolean isReceipt() {
    return getSenderLab() != null;
  }

  public boolean isDistribution() {
    return getRecipient() != null;
  }

  @Override
  public String getDeleteType() {
    return "Transfer";
  }

  @Override
  public String getDeleteDescription() {
    if (isReceipt()) {
      return getDeleteDescription("Receipt", getSenderLab().getAlias(), getRecipientGroup().getName());
    } else if (isDistribution()) {
      return getDeleteDescription("Distribution", getSenderGroup().getName(), getRecipient());
    } else {
      return getDeleteDescription("Internal", getSenderGroup().getName(), getRecipientGroup().getName());
    }
  }

  private String getDeleteDescription(String transferType, String sender, String recipient) {
    int itemCount = getSampleTransfers().size() + getLibraryTransfers().size() + getLibraryAliquotTransfers().size()
        + getPoolTransfers().size();
    return String.format("%s: %s → %s (%d items)", transferType, sender, recipient, itemCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipient, recipientGroup, senderGroup, senderLab, transferTime, transferRequestName);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        Transfer::getRecipient,
        Transfer::getRecipientGroup,
        Transfer::getSenderGroup,
        Transfer::getSenderLab,
        Transfer::getTransferTime,
        Transfer::getTransferRequestName);
  }

}
