package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;

@Entity
public class PoreVersion implements Serializable, Aliasable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long poreVersionId = UNSAVED_ID;

  private String alias;

  @Override
  public long getId() {
    return poreVersionId;
  }

  @Override
  public void setId(long id) {
    this.poreVersionId = id;
  }

  @Override
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PoreVersion other = (PoreVersion) obj;
    if (alias == null) {
      if (other.alias != null)
        return false;
    } else if (!alias.equals(other.alias))
      return false;
    if (poreVersionId != other.poreVersionId)
      return false;
    return true;
  }

  @Override
  public boolean isSaved() {
    return getId() == UNSAVED_ID;
  }

}
