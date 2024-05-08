package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RequisitionChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControllable;
import uk.ac.bbsrc.tgac.miso.core.data.qc.RequisitionQC;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Requisition implements Attachable, Deletable, QualityControllable<RequisitionQC>, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long requisitionId = UNSAVED_ID;

  private String alias;

  @ManyToMany
  @JoinTable(name = "Requisition_Assay", joinColumns = {@JoinColumn(name = "requisitionId")},
      inverseJoinColumns = {@JoinColumn(name = "assayId")})
  private Set<Assay> assays;

  private boolean stopped = false;
  private String stopReason;

  @OneToMany(targetEntity = RequisitionQC.class, mappedBy = "requisition", cascade = CascadeType.ALL)
  private Collection<RequisitionQC> qcs = new TreeSet<>();

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Requisition_Note", joinColumns = {
      @JoinColumn(name = "requisitionId")},
      inverseJoinColumns = {
          @JoinColumn(name = "noteId")})
  private Collection<Note> notes = new HashSet<>();

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

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "requisitionId", nullable = false)
  private List<RequisitionPause> pauses;

  @OneToMany(targetEntity = RequisitionChangeLog.class, mappedBy = "requisition", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @OneToMany(targetEntity = FileAttachment.class)
  @JoinTable(name = "Requisition_Attachment", joinColumns = {@JoinColumn(name = "requisitionId")},
      inverseJoinColumns = {
          @JoinColumn(name = "attachmentId")})
  private List<FileAttachment> attachments;

  @Transient
  private List<FileAttachment> pendingAttachmentDeletions;

  @Override
  public long getId() {
    return requisitionId;
  }

  @Override
  public void setId(long id) {
    this.requisitionId = id;
  }

  @Override
  public boolean isSaved() {
    return requisitionId != UNSAVED_ID;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Set<Assay> getAssays() {
    if (assays == null) {
      assays = new HashSet<>();
    }
    return assays;
  }

  public void setAssays(Set<Assay> assays) {
    this.assays = assays;
  }

  public boolean isStopped() {
    return stopped;
  }

  public void setStopped(boolean stopped) {
    this.stopped = stopped;
  }

  public String getStopReason() {
    return stopReason;
  }

  public void setStopReason(String stopReason) {
    this.stopReason = stopReason;
  }

  @Override
  public QcTarget getQcTarget() {
    return QcTarget.Requisition;
  }

  @Override
  public Collection<RequisitionQC> getQCs() {
    return qcs;
  }

  public void setQCs(Collection<RequisitionQC> qcs) {
    this.qcs = qcs;
  }

  public Collection<Note> getNotes() {
    return notes;
  }

  public void addNote(Note note) {
    this.notes.add(note);
  }

  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  public void updateFromQc(QcCorrespondingField correspondingField, BigDecimal value, String units) {
    correspondingField.updateField(this, value, units);
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
    RequisitionChangeLog change = new RequisitionChangeLog();
    change.setRequisition(this);
    change.setSummary(summary);
    change.setColumnsChanged(columnsChanged);
    change.setUser(user);
    return change;
  }

  public List<RequisitionPause> getPauses() {
    if (pauses == null) {
      pauses = new ArrayList<>();
    }
    return pauses;
  }

  public void setPauses(List<RequisitionPause> pauses) {
    this.pauses = pauses;
  }

  @Override
  public String getDeleteType() {
    return "Requisition";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public List<FileAttachment> getAttachments() {
    return attachments;
  }

  @Override
  public void setAttachments(List<FileAttachment> attachments) {
    this.attachments = attachments;
  }

  @Override
  public String getAttachmentsTarget() {
    return "requisition";
  }

  @Override
  public List<FileAttachment> getPendingAttachmentDeletions() {
    return pendingAttachmentDeletions;
  }

  @Override
  public void setPendingAttachmentDeletions(List<FileAttachment> pendingAttachmentDeletions) {
    this.pendingAttachmentDeletions = pendingAttachmentDeletions;
  }

  @Override
  public int hashCode() {
    return Objects.hash(requisitionId, alias, assays, stopped);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        Requisition::getId,
        Requisition::getAlias,
        Requisition::getAssays,
        Requisition::isStopped);
  }

}
