package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

@Entity
@Table(name = "PoolOrder")
public class PoolOrderImpl implements PoolOrder, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long poolOrderId;

  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "poolId", nullable = false)
  private Pool pool;

  @Column(nullable = false)
  private Integer partitions;

  @ManyToOne
  @JoinColumn(name = "parametersId", nullable = true)
  private SequencingParameters parameters;

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

  @Override
  public long getId() {
    return poolOrderId;
  }

  @Override
  public void setId(Long id) {
    this.poolOrderId = id;
  }

  @Override
  public Pool getPool() {
    return pool;
  }

  @Override
  public void setPool(Pool pool) {
    this.pool = pool;
  }

  @Override
  public Integer getPartitions() {
    return partitions;
  }

  @Override
  public void setPartitions(Integer partitions) {
    this.partitions = partitions;
  }

  @Override
  public SequencingParameters getSequencingParameter() {
    return parameters;
  }

  @Override
  public void setSequencingParameter(SequencingParameters parameters) {
    this.parameters = parameters;
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
  public String getDeleteType() {
    return "Pool Order";
  }

  @Override
  public String getDeleteDescription() {
    return "Pool " + getPool().getId() + " - "
        + getPartitions() + " partitions of "
        + getSequencingParameter().getName();
  }

  @Override
  public SecurityProfile getDeletionSecurityProfile() {
    return null;
  }

}
