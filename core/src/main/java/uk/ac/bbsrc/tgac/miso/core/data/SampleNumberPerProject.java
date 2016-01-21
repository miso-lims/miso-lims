package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface SampleNumberPerProject {

  Long getSampleNumberPerProjectId();

  void setSampleNumberPerProjectId(Long sampleNumberPerProjectId);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  Project getProject();

  void setProject(Project project);

  Integer getHighestSampleNumber();

  void setHighestSampleNumber(Integer highestSampleNumber);

  Integer getPadding();

  void setPadding(Integer padding);

}