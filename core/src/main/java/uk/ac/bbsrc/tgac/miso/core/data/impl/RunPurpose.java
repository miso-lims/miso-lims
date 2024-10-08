package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;

@Entity
public class RunPurpose implements Deletable, Aliasable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long purposeId;

  private String alias;

  @Override
  public long getId() {
    return purposeId;
  }

  @Override
  public void setId(long id) {
    this.purposeId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String getDeleteType() {
    return "Run Purpose";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alias == null) ? 0 : alias.hashCode());
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
    RunPurpose other = (RunPurpose) obj;
    if (alias == null) {
      if (other.alias != null)
        return false;
    } else if (!alias.equals(other.alias))
      return false;
    return true;
  }

}
