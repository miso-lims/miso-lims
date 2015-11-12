package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

@Entity
@Table(name = "SampleAnalyte")
public class SampleAnalyteImpl implements SampleAnalyte {

  @Id
  @GeneratedValue
  private Long sampleAnalyteId;

  @OneToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sampleId", nullable = false)
  private Sample sample;

  @OneToOne(targetEntity = SamplePurposeImpl.class)
  @JoinColumn(name = "samplePurposeId")
  private SamplePurpose samplePurpose;

  @OneToOne(targetEntity = SampleGroupImpl.class)
  @JoinColumn(name = "sampleGroupId")
  private SampleGroupId sampleGroup;

  @OneToOne(targetEntity = TissueMaterialImpl.class)
  @JoinColumn(name = "tissueMaterialId")
  private TissueMaterial tissueMaterial;

  private String region;
  private String tubeId;
  private Integer stockNumber;
  private Integer aliquotNumber;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  private Date creationDate;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
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
  public SamplePurpose getSamplePurpose() {
    return samplePurpose;
  }

  @Override
  public void setSamplePurpose(SamplePurpose samplePurpose) {
    this.samplePurpose = samplePurpose;
  }

  @Override
  public SampleGroupId getSampleGroup() {
    return sampleGroup;
  }

  public void setSampleGroup(SampleGroupId sampleGroup) {
    this.sampleGroup = sampleGroup;
  }

  @Override
  public TissueMaterial getTissueMaterial() {
    return tissueMaterial;
  }

  @Override
  public void setTissueMaterial(TissueMaterial tissueMaterial) {
    this.tissueMaterial = tissueMaterial;
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
  public User getCreatedBy() {
    return createdBy;
  }

  @Override
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
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
  public User getUpdatedBy() {
    return updatedBy;
  }

  @Override
  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
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
