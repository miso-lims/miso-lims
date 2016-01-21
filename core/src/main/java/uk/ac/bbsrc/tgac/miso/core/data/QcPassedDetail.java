package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface QcPassedDetail {

  Long getQcPassedDetailId();

  void setQcPassedDetailId(Long qcPassedDetailsId);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  String getStatus();

  void setStatus(String status);

  String getDescription();

  void setDescription(String description);

  Boolean getNoteRequired();

  void setNoteRequired(Boolean noteRequired);

}