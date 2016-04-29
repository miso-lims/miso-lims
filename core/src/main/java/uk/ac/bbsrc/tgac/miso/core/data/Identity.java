package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.eaglegenomics.simlims.core.User;

@JsonIgnoreProperties({ "sample" })
public interface Identity {
  
  public static final String CATEGORY_NAME = "Identity";

  Long getSampleId();

  void setSampleId(Long sampleId);

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