package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.eaglegenomics.simlims.core.User;

@JsonIgnoreProperties({ "sample" })
public interface SampleTissue {
  
  public static final String CATEGORY_NAME = "Tissue";

  Long getId();

  void setId(Long sampleId);

  Sample getSample();

  void setSample(Sample sample);

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
