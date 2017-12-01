package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PoreVersion implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long poreVersionId = UNSAVED_ID;

  private String alias;

  public long getId() {
    return poreVersionId;
  }

  public void setId(long id) {
    this.poreVersionId = id;
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
    result = prime * result + (int) (poreVersionId ^ (poreVersionId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PoreVersion other = (PoreVersion) obj;
    if (alias == null) {
      if (other.alias != null) return false;
    } else if (!alias.equals(other.alias)) return false;
    if (poreVersionId != other.poreVersionId) return false;
    return true;
  }

}
