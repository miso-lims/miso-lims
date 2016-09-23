package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface DetailedQcStatus {

  Long getId();

  void setId(Long detailedQcStatusId);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  Boolean getStatus();

  void setStatus(Boolean status);

  String getDescription();

  void setDescription(String description);

  Boolean getNoteRequired();

  void setNoteRequired(Boolean noteRequired);

}