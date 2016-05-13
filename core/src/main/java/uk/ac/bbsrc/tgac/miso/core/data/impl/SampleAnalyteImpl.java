package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
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
  private Long sampleId;

  @OneToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sampleId", nullable = false)
  @MapsId
  private Sample sample;

  @OneToOne(targetEntity = SamplePurposeImpl.class)
  @JoinColumn(name = "samplePurposeId")
  private SamplePurpose samplePurpose;

  private Long groupId;
  private String groupDescription;

  @OneToOne(targetEntity = TissueMaterialImpl.class)
  @JoinColumn(name = "tissueMaterialId")
  private TissueMaterial tissueMaterial;

  @Enumerated(EnumType.STRING)
  private StrStatus strStatus = StrStatus.NOT_SUBMITTED;

  private String region;
  private String tubeId;

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
  public Long getId() {
    return sampleId;
  }

  @Override
  public void setId(Long sampleId) {
    this.sampleId = sampleId;
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
  public Long getGroupId() {
    return groupId;
  }

  @Override
  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }

  @Override
  public String getGroupDescription() {
    return groupDescription;
  }

  @Override
  public void setGroupDescription(String groupDescription) {
    this.groupDescription = groupDescription;
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

  @Override
  public StrStatus getStrStatus() {
    return strStatus;
  }

  @Override
  public void setStrStatus(StrStatus strStatus) {
    this.strStatus = strStatus;
  }

  @Override
  public void setStrStatus(String strStatus) {
    this.strStatus = StrStatus.get(strStatus);
  }

  @Override
  public String toString() {
    return "SampleAnalyteImpl [sampleId=" + sampleId + ", sample=" + sample
        + ", samplePurpose=" + samplePurpose + ", groupId=" + groupId + ", groupDescription=" + groupDescription
        + ", tissueMaterial=" + tissueMaterial + ", strStatus=" + strStatus
        + ", region=" + region + ", tubeId=" + tubeId + ", createdBy="
        + createdBy + ", creationDate=" + creationDate + ", updatedBy="
        + updatedBy + ", lastUpdated=" + lastUpdated + "]";
  }

}
