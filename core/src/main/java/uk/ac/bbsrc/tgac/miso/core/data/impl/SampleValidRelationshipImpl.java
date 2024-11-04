package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

@Entity
@Table(name = "SampleValidRelationship", uniqueConstraints = @UniqueConstraint(columnNames = {"parentId", "childId"}))
public class SampleValidRelationshipImpl implements SampleValidRelationship, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long sampleValidRelationshipId = UNSAVED_ID;

  @ManyToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "parentId", nullable = false)
  private SampleClass parent;

  @ManyToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "childId", nullable = false)
  private SampleClass child;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @Column(nullable = false)
  private boolean archived;

  @Override
  public long getId() {
    return sampleValidRelationshipId;
  }

  @Override
  public void setId(long sampleValidRelationshipId) {
    this.sampleValidRelationshipId = sampleValidRelationshipId;
  }

  @Override
  public User getCreator() {
    return createdBy;
  }

  @Override
  public void setCreator(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public Date getCreationTime() {
    return creationDate;
  }

  @Override
  public void setCreationTime(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public User getLastModifier() {
    return updatedBy;
  }

  @Override
  public void setLastModifier(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public Date getLastModified() {
    return lastUpdated;
  }

  @Override
  public void setLastModified(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public SampleClass getParent() {
    return parent;
  }

  @Override
  public void setParent(SampleClass parent) {
    this.parent = parent;
  }

  @Override
  public SampleClass getChild() {
    return child;
  }

  @Override
  public void setChild(SampleClass child) {
    this.child = child;
  }

  @Override
  public boolean isArchived() {
    return archived;
  }

  @Override
  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

}
