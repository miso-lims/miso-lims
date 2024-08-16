package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolOrderChangeLog;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class PoolOrder implements Deletable, Serializable, ChangeLoggable {

  public enum Status {
    OUTSTANDING("Outstanding"), DRAFT("Draft"), FULFILLED("Fulfilled");

    private final String label;

    private Status(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }

    public static Status get(String label) {
      for (Status s : Status.values()) {
        if (s.getLabel().equals(label)) {
          return s;
        }
      }
      return null;
    }
  }

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long poolOrderId = UNSAVED_ID;

  private String alias;

  @ManyToOne
  @JoinColumn(name = "purposeId")
  private RunPurpose purpose;

  private String description;

  @OneToMany(mappedBy = "poolOrder", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<OrderLibraryAliquot> orderLibraryAliquots;

  @ManyToOne
  @JoinColumn(name = "parametersId")
  private SequencingParameters parameters;

  private Integer partitions;

  @ManyToOne
  @JoinColumn(name = "sequencingContainerModelId")
  private SequencingContainerModel containerModel;

  private boolean draft = false;

  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "poolId")
  private Pool pool;

  @ManyToOne(targetEntity = SequencingOrderImpl.class)
  @JoinColumn(name = "sequencingOrderId")
  private SequencingOrder sequencingOrder;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @OneToMany(targetEntity = PoolOrderChangeLog.class, mappedBy = "poolOrder", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @Override
  public long getId() {
    return poolOrderId;
  }

  @Override
  public void setId(long id) {
    this.poolOrderId = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public RunPurpose getPurpose() {
    return this.purpose;
  }

  public void setPurpose(RunPurpose purpose) {
    this.purpose = purpose;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<OrderLibraryAliquot> getOrderLibraryAliquots() {
    if (orderLibraryAliquots == null) {
      orderLibraryAliquots = new HashSet<>();
    }
    return orderLibraryAliquots;
  }

  public void setOrderLibraryAliquots(Set<OrderLibraryAliquot> orderLibraryAliquots) {
    this.orderLibraryAliquots = orderLibraryAliquots;
  }

  public SequencingParameters getParameters() {
    return parameters;
  }

  public void setParameters(SequencingParameters parameters) {
    this.parameters = parameters;
  }

  public Integer getPartitions() {
    return partitions;
  }

  public void setPartitions(Integer partitions) {
    this.partitions = partitions;
  }

  public SequencingContainerModel getContainerModel() {
    return containerModel;
  }

  public void setContainerModel(SequencingContainerModel containerModel) {
    this.containerModel = containerModel;
  }

  public boolean isDraft() {
    return draft;
  }

  public void setDraft(boolean draft) {
    this.draft = draft;
  }

  public Pool getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

  public SequencingOrder getSequencingOrder() {
    return sequencingOrder;
  }

  public void setSequencingOrder(SequencingOrder sequencingOrder) {
    this.sequencingOrder = sequencingOrder;
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
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    PoolOrderChangeLog changeLog = new PoolOrderChangeLog();
    changeLog.setPoolOrder(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Pool Order";
  }

  @Override
  public String getDeleteDescription() {
    if (getPartitions() != null) {
      return String.format("%d partitions of %s (%d aliquots)", getPartitions(), getParameters().getName(),
          getOrderLibraryAliquots() == null ? 0 : getOrderLibraryAliquots().size());
    } else {
      return String.format("%d aliquots", getOrderLibraryAliquots().size());
    }
  }

  public Status getStatus() {
    if (getPool() != null && (getParameters() == null || getSequencingOrder() != null)) {
      return Status.FULFILLED;
    } else if (isDraft()) {
      return Status.DRAFT;
    } else {
      return Status.OUTSTANDING;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, description, draft, orderLibraryAliquots, parameters, partitions, containerModel, pool,
        purpose,
        sequencingOrder);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        PoolOrder::getAlias,
        PoolOrder::getDescription,
        PoolOrder::isDraft,
        PoolOrder::getOrderLibraryAliquots,
        PoolOrder::getParameters,
        PoolOrder::getPartitions,
        PoolOrder::getContainerModel,
        PoolOrder::getPool,
        PoolOrder::getPurpose,
        PoolOrder::getSequencingOrder);
  }

  public String getLongestIndex() {
    return LimsUtils.getLongestIndex(getOrderLibraryAliquots().stream()
        .map(orderAliquot -> orderAliquot.getAliquot().getLibrary())
        .collect(Collectors.toList()));
  }
}
