package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

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

  @ManyToOne
  @JoinColumn(name = "sequencingContainerModelId")
  private SequencingContainerModel containerModel;

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
  public String getDescription() {
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
  public SequencingContainerModel getContainerModel() {
    return containerModel;
  }

  @Override
  public void setContainerModel(SequencingContainerModel containerModel) {
    this.containerModel = containerModel;
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
    return Objects.hash(description, parameters, partitions, pool, purpose, containerModel);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        SequencingOrderImpl::getDescription,
        SequencingOrderImpl::getSequencingParameter,
        SequencingOrderImpl::getPartitions,
        SequencingOrderImpl::getPool,
        SequencingOrderImpl::getPurpose,
        SequencingOrderImpl::getContainerModel);
  }

}
