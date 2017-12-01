package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FlowCellVersion implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long flowCellVersionId = UNSAVED_ID;

  private String alias;

  public long getId() {
    return flowCellVersionId;
  }

  public void setId(long id) {
    this.flowCellVersionId = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alias == null) ? 0 : alias.hashCode());
    result = prime * result + (int) (flowCellVersionId ^ (flowCellVersionId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    FlowCellVersion other = (FlowCellVersion) obj;
    if (alias == null) {
      if (other.alias != null) return false;
    } else if (!alias.equals(other.alias)) return false;
    if (flowCellVersionId != other.flowCellVersionId) return false;
    return true;
  }

}
