package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface Subproject extends Serializable, Aliasable, Deletable {

  void setAlias(String alias);

  String getDescription();

  void setDescription(String description);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  Project getParentProject();

  void setParentProject(Project parentProject);

  Boolean getPriority();

  void setPriority(Boolean priority);

  void setReferenceGenomeId(Long referenceGenomeId);

  Long getReferenceGenomeId();

}