package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface Identity {

  Long getIdentityId();

  void setIdentityId(Long identityId);

  Sample getSample();

  void setSample(Sample sample);

  String getInternalName();

  void setInternalName(String internalName);

  String getExternalName();

  void setExternalName(String externalName);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

}