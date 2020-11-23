package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Run")
@Immutable
public class RunQcNode implements QcNode {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "runId")
  private long id;

  private String name;
  private String alias;

  @Enumerated(EnumType.STRING)
  private HealthType health;

  @Transient
  private List<RunPartitionQcNode> runPartitions;

  @Override
  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override
  public QcNodeType getEntityType() {
    return QcNodeType.RUN;
  }

  @Override
  public String getTypeLabel() {
    return QcNodeType.RUN.getLabel();
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String getLabel() {
    return getAlias();
  }

  @Override
  public Boolean getQcPassed() {
    if (getHealth() == HealthType.Completed) {
      return true;
    } else if (getHealth() == HealthType.Failed) {
      return false;
    } else {
      return null;
    }
  }

  @Override
  public Long getQcStatusId() {
    return null;
  }

  @Override
  public String getQcNote() {
    return null;
  }

  public HealthType getHealth() {
    return health;
  }

  public void setHealth(HealthType health) {
    this.health = health;
  }

  public List<RunPartitionQcNode> getRunPartitions() {
    if (runPartitions == null) {
      runPartitions = new ArrayList<>();
    }
    return runPartitions;
  }

  public void setRunPartitions(List<RunPartitionQcNode> runPartitions) {
    this.runPartitions = runPartitions;
  }

  @Override
  public String getRunStatus() {
    return health == null ? null : health.getKey();
  }

  @Override
  public List<? extends QcNode> getChildren() {
    return getRunPartitions();
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, health, id, name);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        RunQcNode::getAlias,
        RunQcNode::getHealth,
        RunQcNode::getId,
        RunQcNode::getName);
  }

}
