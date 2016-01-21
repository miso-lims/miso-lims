package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface SampleGroupId {

  Long getSampleGroupId();

  void setSampleGroupId(Long sampleGroupId);

  Project getProject();

  void setProject(Project project);

  Integer getGroupId();

  void setGroupId(Integer groupId);

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

}