package uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
@Table(name = "Transfer")
@Immutable
public class ListTransferView implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
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

  @OneToMany(mappedBy = "transfer")
  private Set<ListTransferViewSample> samples;

  @OneToMany(mappedBy = "transfer")
  private Set<ListTransferViewLibrary> libraries;

  @OneToMany(mappedBy = "transfer")
  private Set<ListTransferViewLibraryAliquot> libraryAliquots;

  @OneToMany(mappedBy = "transfer")
  private Set<ListTransferViewPool> pools;

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

  public User getCreator() {
    return creator;
  }

  public void setCreator(User user) {
    this.creator = user;
  }

  public Date getCreationTime() {
    return created;
  }

  public void setCreationTime(Date creationTime) {
    this.created = creationTime;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setLastModifier(User user) {
    this.lastModifier = user;
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

  public Set<ListTransferViewSample> getSamples() {
    if (samples == null) {
      samples = new HashSet<>();
    }
    return samples;
  }

  public Set<ListTransferViewLibrary> getLibraries() {
    if (libraries == null) {
      libraries = new HashSet<>();
    }
    return libraries;
  }

  public Set<ListTransferViewLibraryAliquot> getLibraryAliquots() {
    if (libraryAliquots == null) {
      libraryAliquots = new HashSet<>();
    }
    return libraryAliquots;
  }

  public Set<ListTransferViewPool> getPools() {
    if (pools == null) {
      pools = new HashSet<>();
    }
    return pools;
  }

  public Set<String> getProjectLabels() {
    return Stream.of(getSamples(), getLibraries(), getLibraryAliquots(), getPools())
        .flatMap(Collection::stream)
        .map(ListTransferViewItem::getProjectLabel)
        .collect(Collectors.toSet());
  }

  public long getItems() {
    return countAllItems(item -> true);
  }

  public long getReceived() {
    return countAllItems(item -> Boolean.TRUE.equals(item.isReceived()));
  }

  public long getReceiptPending() {
    return countAllItems(item -> item.isReceived() == null);
  }

  public long getQcPassed() {
    return countAllItems(item -> Boolean.TRUE.equals(item.isQcPassed()));
  }

  public long getQcPending() {
    return countAllItems(item -> item.isQcPassed() == null);
  }

  private long countAllItems(Predicate<ListTransferViewItem> predicate) {
    return countItems(getSamples(), predicate) + countItems(getLibraries(), predicate)
        + countItems(getLibraryAliquots(), predicate) + countItems(getPools(), predicate);
  }

  private static long countItems(Collection<? extends ListTransferViewItem> items,
      Predicate<ListTransferViewItem> predicate) {
    return items.stream().filter(predicate).count();
  }

}
