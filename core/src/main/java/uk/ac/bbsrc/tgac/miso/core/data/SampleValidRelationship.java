package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface SampleValidRelationship {

  Long getSampleValidRelationshipId();

  void setSampleValidRelationshipId(Long sampleValidRelationshipId);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  SampleClass getParent();

  void setParent(SampleClass parent);

  SampleClass getChild();

  void setChild(SampleClass child);

}