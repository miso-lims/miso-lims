package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

@Entity
@Table(name = "TissueMaterial")
public class TissueMaterialImpl implements TissueMaterial, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tissueMaterialId;

  @Column(unique = true, nullable = false)
  private String alias;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @Override
  public Long getId() {
    return tissueMaterialId;
  }

  @Override
  public void setId(Long tissueMaterialId) {
    this.tissueMaterialId = tissueMaterialId;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
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

}
