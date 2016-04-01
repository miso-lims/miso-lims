package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;

@Entity
@Table(name = "SampleTissue")
public class SampleTissueImpl implements SampleTissue {

  @Id
  private Long sampleId;

  @OneToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sampleId", nullable = false)
  @MapsId
  private Sample sample;

  private String instituteTissueName;
  private Integer cellularity;

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
  public Sample getSample() {
    return sample;
  }

  @Override
  public void setSample(Sample sample) {
    this.sample = sample;
  }

  @Override
  public String getInstituteTissueName() {
    return instituteTissueName;
  }

  @Override
  public void setInstituteTissueName(String instituteTissueName) {
    this.instituteTissueName = instituteTissueName;
  }

  @Override
  public Integer getCellularity() {
    return cellularity;
  }

  @Override
  public void setCellularity(Integer cellularity) {
    this.cellularity = cellularity;
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
  public Long getSampleId() {
    return sampleId;
  }

  @Override
  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
  }

  @Override
  public String toString() {
    return "SampleTissueImpl [sampleId=" + sampleId + ", sample=" + sample + ", instituteTissueName=" + instituteTissueName
        + ", cellularity=" + cellularity + ", createdBy=" + createdBy + ", creationDate=" + creationDate + ", updatedBy=" + updatedBy
        + ", lastUpdated=" + lastUpdated + "]";
  }

}
