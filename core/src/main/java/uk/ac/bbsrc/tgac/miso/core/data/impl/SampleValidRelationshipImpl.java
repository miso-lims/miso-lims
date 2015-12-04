package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

@Entity
@Table(name = "SampleValidRelationship", uniqueConstraints = @UniqueConstraint(columnNames = { "parentId", "childId" }) )
public class SampleValidRelationshipImpl implements SampleValidRelationship {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sampleValidRelationshipId;

  @OneToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "parentId", nullable = false)
  private SampleClass parent;

  @OneToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "childId", nullable = false)
  private SampleClass child;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  private Date creationDate;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  private Date lastUpdated;

  @Override
  public Long getSampleValidRelationshipId() {
    return sampleValidRelationshipId;
  }

  @Override
  public void setSampleValidRelationshipId(Long sampleValidRelationshipId) {
    this.sampleValidRelationshipId = sampleValidRelationshipId;
  }

  @Override
  public User getCreatedBy() {
    return createdBy;
  }

  @Override
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public User getUpdatedBy() {
    return updatedBy;
  }

  @Override
  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
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

}
