package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface SampleTissue {

  Long getId();

  void setId(Long id);

  Sample getSample();

  void setSample(Sample sample);

  Lab getLab();

  void setLab(Lab lab);

  /**
   * @return the tissue's identifying name or ID at the source Institute
   */
  String getInstituteTissueName();

  /**
   * Sets the tissue's identifying name or ID at the source Institute
   * 
   * @param instituteTissueName
   */
  void setInstituteTissueName(String instituteTissueName);

  Integer getCellularity();

  void setCellularity(Integer cellularity);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

}
