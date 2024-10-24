package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample.RequisitionSupplementalSampleId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * This class exists mainly as a Hibernate optimization. With this, we can easily retrieve and
 * modify which supplemental samples are linked to the requisition. Excluding them from the
 * Requisition model itself eliminates the chance of performance impacts from inadvertent retrieval
 */
@Entity
@Table(name = "Requisition_SupplementalSample")
@IdClass(RequisitionSupplementalSampleId.class)
public class RequisitionSupplementalSample implements Serializable {

  private static final long serialVersionUID = 1L;

  public static class RequisitionSupplementalSampleId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long requisitionId;

    private Sample sample;

    public Long getRequisitionId() {
      return requisitionId;
    }

    public void setRequisitionId(Long requisitionId) {
      this.requisitionId = requisitionId;
    }

    public Sample getSample() {
      return sample;
    }

    public void setSample(Sample sample) {
      this.sample = sample;
    }

    private Long getSampleId() {
      return sample == null ? null : sample.getId();
    }

    @Override
    public int hashCode() {
      return Objects.hash(requisitionId, sample);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          RequisitionSupplementalSampleId::getRequisitionId,
          RequisitionSupplementalSampleId::getSampleId);
    }

  }

  @Id
  @Column(nullable = false, updatable = false)
  private Long requisitionId;

  @Id
  @ManyToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sampleId", nullable = false, updatable = false)
  private Sample sample;

  public RequisitionSupplementalSample() {
    // default constructor
  }

  public RequisitionSupplementalSample(long requisitionId, Sample sample) {
    this.requisitionId = requisitionId;
    this.sample = sample;
  }

  public Long getRequisitionId() {
    return requisitionId;
  }

  public void setRequisitionId(Long requisitionId) {
    this.requisitionId = requisitionId;
  }

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  @Override
  public int hashCode() {
    return Objects.hash(requisitionId, sample);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        RequisitionSupplementalSample::getRequisitionId,
        RequisitionSupplementalSample::getSample);
  }

}
