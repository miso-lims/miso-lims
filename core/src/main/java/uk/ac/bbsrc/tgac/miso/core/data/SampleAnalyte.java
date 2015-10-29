package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface SampleAnalyte {
  public static final Long UNSAVED_ID = 0L;

  public Long getSampleAnalyteId();

  public void setSampleAnalyteId(Long sampleAnalyteId);

  public Sample getSample();

  public void setSample(Sample sample);

  public String getPurpose();

  public void setPurpose(String purpose);

  public String getRegion();

  public void setRegion(String region);

  public String getTubeId();

  public void setTubeId(String tubeId);

  public Integer getStockNumber();

  public void setStockNumber(Integer stockNumber);

  public Integer getAliquotNumber();

  public void setAliquotNumber(Integer aliquotNumber);

  public Date getCreationDate();

  public void setCreationDate(Date creationDate);

  public Date getLastUpdated();

  public void setLastUpdated(Date lastUpdated);

}
