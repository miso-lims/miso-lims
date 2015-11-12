package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface Subproject {

  Long getSubprojectId();

  void setSubprojectId(Long subprojectId);

  String getAlias();

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

}