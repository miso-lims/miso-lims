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

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;

@Entity
@Table(name = "Identity")
public class IdentityImpl implements Identity {

  @Id
  private Long sampleId;

  @OneToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sampleId", nullable = false)
  @MapsId
  private Sample sample;

  @Column(unique = true, nullable = false)
  private String internalName;

  @Column(nullable = false, unique = true)
  private String externalName;
  
  @Enumerated(EnumType.STRING)
  private DonorSex donorSex = DonorSex.UNKNOWN;

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
  public Long getSampleId() {
    return sampleId;
  }

  @Override
  public void setSampleId(Long sampleId) {
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
  public String getInternalName() {
    return internalName;
  }

  @Override
  public void setInternalName(String internalName) {
    this.internalName = internalName;
  }

  @Override
  public String getExternalName() {
    return externalName;
  }

  @Override
  public void setExternalName(String externalName) {
    this.externalName = externalName;
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
  public DonorSex getDonorSex() {
    return donorSex;
  }

  @Override
  public void setDonorSex(DonorSex donorSex) {
    this.donorSex = donorSex;
  }

  @Override
  public void setDonorSex(String donorSex) {
    this.donorSex = DonorSex.get(donorSex);
  }

  @Override
  public String toString() {
    return "IdentityImpl [sampleId=" + sampleId + ", sample=" + sample + ", internalName=" + internalName 
        + ", externalName=" + externalName + ", donorSex=" + donorSex + ", createdBy=" + createdBy
        + ", creationDate=" + creationDate + ", updatedBy=" + updatedBy + ", lastUpdated=" + lastUpdated + "]";
  }

}
