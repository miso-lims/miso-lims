package uk.ac.bbsrc.tgac.miso.core.data.impl.transfer;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
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

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Timestamped;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
public class Transfer implements Identifiable, Timestamped, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long transferId;

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

}
