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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

@Entity
@Table(name = "PoolOrder")
public class PoolOrderImpl implements PoolOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long poolOrderId;

  @Column(nullable = false)
  private Long poolId;

  @Column(nullable = false)
  private Integer partitions;

  @OneToOne(targetEntity = SequencingParametersImpl.class)
  @JoinColumn(name = "parametersId", nullable = true)
  private SequencingParameters parameters;

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
    return poolOrderId;
  }

  @Override
  public void setId(Long id) {
    this.poolOrderId = id;
  }

  @Override
  public Long getPoolId() {
    return poolId;
  }

  @Override
  public void setPoolId(Long poolId) {
    this.poolId = poolId;
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

}
