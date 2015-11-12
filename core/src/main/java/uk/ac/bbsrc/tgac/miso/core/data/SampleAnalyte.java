package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface SampleAnalyte {

  Long getSampleAnalyteId();

  void setSampleAnalyteId(Long sampleAnalyteId);

  Sample getSample();

  void setSample(Sample sample);

  SamplePurpose getSamplePurpose();

  void setSamplePurpose(SamplePurpose samplePurpose);

  SampleGroupId getSampleGroup();

  void setSampleGroup(SampleGroupId sampleGroup);

  TissueMaterial getTissueMaterial();

  void setTissueMaterial(TissueMaterial tissueMaterial);

  String getRegion();

  void setRegion(String region);

  String getTubeId();

  void setTubeId(String tubeId);

  Integer getStockNumber();

  void setStockNumber(Integer stockNumber);

  Integer getAliquotNumber();

  void setAliquotNumber(Integer aliquotNumber);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

}