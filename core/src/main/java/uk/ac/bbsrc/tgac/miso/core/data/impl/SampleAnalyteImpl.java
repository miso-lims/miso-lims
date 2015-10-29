package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;

@Entity
public class SampleAnalyteImpl implements SampleAnalyte {

  @Id
  @GeneratedValue
  private Long sampleAnalyteId;
  private Sample sample;

  private String purpose;
  private String region;
  private String tubeId;
  // private Integer groupId;
  // private String groupDescription;
  private Integer stockNumber;
  private Integer aliquotNumber;

  private Date creationDate;
  private Date lastUpdated;

  @Override
  public Long getSampleAnalyteId() {
    return sampleAnalyteId;
  }

  @Override
  public void setSampleAnalyteId(Long sampleAnalyteId) {
    this.sampleAnalyteId = sampleAnalyteId;
  }

  @Override
  public Sample getSample() {
    return sample;
  }

  @Override
  public void setSample(Sample sample) {
    this.sample = sample;
  }

  @Override
  public String getPurpose() {
    return purpose;
  }

  @Override
  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  @Override
  public String getRegion() {
    return region;
  }

  @Override
  public void setRegion(String region) {
    this.region = region;
  }

  @Override
  public String getTubeId() {
    return tubeId;
  }

  @Override
  public void setTubeId(String tubeId) {
    this.tubeId = tubeId;
  }

  @Override
  public Integer getStockNumber() {
    return stockNumber;
  }

  @Override
  public void setStockNumber(Integer stockNumber) {
    this.stockNumber = stockNumber;
  }

  @Override
  public Integer getAliquotNumber() {
    return aliquotNumber;
  }

  @Override
  public void setAliquotNumber(Integer aliquotNumber) {
    this.aliquotNumber = aliquotNumber;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

}
