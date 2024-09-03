package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Immutable
public class SequencingOrderSummaryView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String orderSummaryId;

  @ManyToOne
  @JoinColumn(name = "poolId")
  private ListPoolView pool;

  @ManyToOne
  @JoinColumn(name = "sequencingContainerModelId")
  private SequencingContainerModel containerModel;

  @ManyToOne
  @JoinColumn(name = "parametersId")
  private SequencingParameters parameters;

  private int requested;
  private int loaded;
  private String description;
  private String purpose;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @OneToMany
  @JoinColumn(name = "orderSummaryId")
  private Set<SequencingOrderPartitionView> partitions;

  @OneToMany
  @JoinColumn(name = "noContainerModelId")
  private Set<SequencingOrderPartitionView> noContainerModelPartitions;

  @OneToOne
  @PrimaryKeyJoinColumn
  private SequencingOrderFulfillmentView fulfillmentView;

  @OneToOne
  @PrimaryKeyJoinColumn
  private SequencingOrderNoContainerModelFulfillmentView noContainerModelFulfillmentView;

  public String getId() {
    return orderSummaryId;
  }

  public void setId(String id) {
    this.orderSummaryId = id;
  }

  public ListPoolView getPool() {
    return pool;
  }

  public void setPool(ListPoolView pool) {
    this.pool = pool;
  }

  public SequencingContainerModel getContainerModel() {
    return containerModel;
  }

  public void setContainerModel(SequencingContainerModel containerModel) {
    this.containerModel = containerModel;
  }

  public SequencingParameters getParameters() {
    return parameters;
  }

  public void setParameters(SequencingParameters parameters) {
    this.parameters = parameters;
  }

  public int getRequested() {
    return requested;
  }

  public void setRequested(int requested) {
    this.requested = requested;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Set<SequencingOrderPartitionView> getPartitions() {
    return partitions;
  }

  public void setPartitions(Set<SequencingOrderPartitionView> partitions) {
    this.partitions = partitions;
  }

  public SequencingOrderFulfillmentView getFulfillmentView() {
    return fulfillmentView;
  }

  public void setFulfillmentView(SequencingOrderFulfillmentView fulfillmentView) {
    this.fulfillmentView = fulfillmentView;
  }

  public Set<SequencingOrderPartitionView> getNoContainerModelPartitions() {
    return noContainerModelPartitions;
  }

  public void setNoContainerModelPartitions(Set<SequencingOrderPartitionView> noContainerModelPartitions) {
    this.noContainerModelPartitions = noContainerModelPartitions;
  }

  public SequencingOrderNoContainerModelFulfillmentView getNoContainerModelFulfillmentView() {
    return noContainerModelFulfillmentView;
  }

  public void setNoContainerModelFulfillmentView(
      SequencingOrderNoContainerModelFulfillmentView noContainerModelFulfillmentView) {
    this.noContainerModelFulfillmentView = noContainerModelFulfillmentView;
  }

  @Override
  public int hashCode() {
    return Objects.hash(containerModel, description, orderSummaryId, parameters, pool, purpose);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        SequencingOrderSummaryView::getContainerModel,
        SequencingOrderSummaryView::getDescription,
        SequencingOrderSummaryView::getId,
        SequencingOrderSummaryView::getParameters,
        SequencingOrderSummaryView::getPool,
        SequencingOrderSummaryView::getPurpose);
  }

  public int getRemaining() {
    return getRequested() - get(HealthType.Completed);
  }

  public int getLoaded() {
    return loaded;
  }

  public void setLoaded(int loaded) {
    this.loaded = loaded;
  }

  public int get(HealthType health) {
    Set<SequencingOrderPartitionView> effectivePartitions;
    if (getContainerModel() == null) {
      effectivePartitions = noContainerModelPartitions;
    } else {
      effectivePartitions = partitions;
    }
    if (effectivePartitions == null) {
      return 0;
    }
    return (int) effectivePartitions.stream().filter(partition -> partition.getHealth() == health).count();
  }

}
