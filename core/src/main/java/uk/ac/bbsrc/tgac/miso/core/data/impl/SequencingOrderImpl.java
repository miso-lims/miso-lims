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

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

@Entity
@Table(name = "SequencingOrder")
public class SequencingOrderImpl implements SequencingOrder, Serializable {

  private static final long serialVersionUID = 1L;
  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long sequencingOrderId = UNSAVED_ID;

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

  @Column(nullable = true, length = 255, name = "description")
  private String description;

  @ManyToOne
  @JoinColumn(name = "purposeId")
  private RunPurpose purpose;

  @Override
  public long getId() {
    return sequencingOrderId;
  }

  @Override
  public void setId(long id) {
    this.sequencingOrderId = id;
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
  public void setSequencingParameters(SequencingParameters parameters) {
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
  public String getDescription(){
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public RunPurpose getPurpose() {
    return this.purpose;
  }

  @Override
  public void setPurpose(RunPurpose purpose) {
    this.purpose = purpose;
  }

  @Override
  public String getDeleteType() {
    return "Sequencing Order";
  }

  @Override
  public String getDeleteDescription() {
    return "Pool " + getPool().getId() + " - "
        + getPartitions() + " partitions of "
        + getSequencingParameter().getName();
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
    result = prime * result + ((partitions == null) ? 0 : partitions.hashCode());
    result = prime * result + ((pool == null) ? 0 : pool.hashCode());
    result = prime * result + ((purpose == null) ? 0 : purpose.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SequencingOrderImpl other = (SequencingOrderImpl) obj;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (parameters == null) {
      if (other.parameters != null) return false;
    } else if (!parameters.equals(other.parameters)) return false;
    if (partitions == null) {
      if (other.partitions != null) return false;
    } else if (!partitions.equals(other.partitions)) return false;
    if (pool == null) {
      if (other.pool != null) return false;
    } else if (!pool.equals(other.pool)) return false;
    if (purpose == null) {
      if (other.purpose != null) return false;
    } else if (!purpose.equals(other.purpose)) return false;
    return true;
  }

}
