package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;

@Entity
public class AttachmentCategory implements Aliasable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long categoryId = UNSAVED_ID;

  private String alias;

  @Override
  public long getId() {
    return categoryId;
  }

  @Override
  public void setId(long id) {
    this.categoryId = id;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public boolean isSaved() {
    return categoryId != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Attachment Category";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public SecurityProfile getDeletionSecurityProfile() {
    return null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alias == null) ? 0 : alias.hashCode());
    result = prime * result + (int) (categoryId ^ (categoryId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AttachmentCategory other = (AttachmentCategory) obj;
    if (alias == null) {
      if (other.alias != null) return false;
    } else if (!alias.equals(other.alias)) return false;
    if (categoryId != other.categoryId) return false;
    return true;
  }

}
