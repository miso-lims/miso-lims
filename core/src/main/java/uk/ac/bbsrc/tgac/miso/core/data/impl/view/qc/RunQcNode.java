package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
  private Boolean qcPassed;
  private Boolean dataReview;

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

  @Override
  public Boolean getDataReview() {
    return dataReview;
  }

  public void setDataReview(Boolean dataReview) {
    this.dataReview = dataReview;
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
  public List<? extends QcNode> getChildren() {
    return getRunPartitions();
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, qcPassed, id, name, dataReview);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        RunQcNode::getAlias,
        RunQcNode::getQcPassed,
        RunQcNode::getId,
        RunQcNode::getName,
        RunQcNode::getDataReview);
  }

}
