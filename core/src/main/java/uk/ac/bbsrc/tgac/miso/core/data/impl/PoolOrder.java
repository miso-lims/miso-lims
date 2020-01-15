package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolOrderChangeLog;

@Entity
public class PoolOrder implements Deletable, Identifiable, Serializable, Timestamped, ChangeLoggable {

  @OneToMany(targetEntity = PoolOrderChangeLog.class, mappedBy = "poolOrder", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

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
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alias == null) ? 0 : alias.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + (draft ? 1231 : 1237);
    result = prime * result + ((orderLibraryAliquots == null) ? 0 : orderLibraryAliquots.hashCode());
    result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
    result = prime * result + ((partitions == null) ? 0 : partitions.hashCode());
    result = prime * result + ((pool == null) ? 0 : pool.hashCode());
    result = prime * result + ((purpose == null) ? 0 : purpose.hashCode());
    result = prime * result + ((sequencingOrder == null) ? 0 : sequencingOrder.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PoolOrder other = (PoolOrder) obj;
    if (alias == null) {
      if (other.alias != null) return false;
    } else if (!alias.equals(other.alias)) return false;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (draft != other.draft) return false;
    if (orderLibraryAliquots == null) {
      if (other.orderLibraryAliquots != null) return false;
    } else if (!orderLibraryAliquots.equals(other.orderLibraryAliquots)) return false;
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
    if (sequencingOrder == null) {
      if (other.sequencingOrder != null) return false;
    } else if (!sequencingOrder.equals(other.sequencingOrder)) return false;
    return true;
  }

  public String getLongestIndex() {
    Map<Integer, Integer> lengths = orderLibraryAliquots.stream()
            .flatMap(element -> element.getAliquot().getLibrary().getIndices().stream())
            .collect(Collectors.toMap(Index::getPosition, index -> index.getSequence().length(), Integer::max));
    if (lengths.isEmpty()) {
      return "0";
    }
    return lengths.entrySet().stream()
            .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
            .map(Map.Entry<Integer, Integer>::getValue)
            .map(length -> length.toString())
            .collect(Collectors.joining(","));
  }
}
