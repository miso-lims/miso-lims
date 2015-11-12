package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

public interface SampleAdditionalInfo {

  Long getSampleAdditionalInfoId();

  void setSampleAdditionalInfoId(Long sampleAdditionalInfoId);

  Sample getSample();

  void setSample(Sample sample);

  SampleClass getSampleClass();

  void setSampleClass(SampleClass sampleClass);

  TissueOrigin getTissueOrigin();

  void setTissueOrigin(TissueOrigin tissueOrigin);

  TissueType getTissueType();

  void setTissueType(TissueType tissueType);

  Subproject getSubproject();

  void setSubproject(Subproject subproject);

  Integer getPassageNumber();

  void setPassageNumber(Integer passageNumber);

  Integer getTimesReceived();

  void setTimesReceived(Integer timesReceived);

  Integer getTubeNumber();

  void setTubeNumber(Integer tubeNumber);

  Double getVolume();

  void setVolume(Double volume);

  Double getConcentration();

  void setConcentration(Double concentration);

  Boolean getArchived();

  void setArchived(Boolean archived);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  QcPassedDetail getQcPassedDetail();

  void setQcPassedDetail(QcPassedDetail qcPassedDetail);

  KitDescriptor getPrepKit();

  void setPrepKit(KitDescriptor prepKit);

}