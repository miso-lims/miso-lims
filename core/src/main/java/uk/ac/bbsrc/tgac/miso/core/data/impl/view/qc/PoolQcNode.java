package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "Pool")
@Immutable
public class PoolQcNode implements QcNode {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "poolId")
  private long id;

  private String name;

  private String alias;

  private Boolean qcPassed;

  @Transient
  private List<RunQcNode> runs;

  @Override
  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override
  public QcNodeType getEntityType() {
    return QcNodeType.POOL;
  }

  @Override
  public String getTypeLabel() {
    return QcNodeType.POOL.getLabel();
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
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  @Override
  public Long getQcStatusId() {
    return null;
  }

  @Override
  public String getQcNote() {
    return null;
  }

  public List<RunQcNode> getRuns() {
    if (runs == null) {
      runs = new ArrayList<>();
    }
    return runs;
  }

  public void setRuns(List<RunQcNode> runs) {
    this.runs = runs;
  }

  @Override
  public List<RunQcNode> getChildren() {
    return getRuns();
  }

}
