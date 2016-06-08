package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

@Entity
@Table(name = "SampleAnalyte")
public class SampleAnalyteImpl extends SampleAdditionalInfoImpl implements SampleAnalyte {

  private static final long serialVersionUID = 1L;

  @OneToOne(targetEntity = SamplePurposeImpl.class)
  @JoinColumn(name = "samplePurposeId")
  private SamplePurpose samplePurpose;

  @OneToOne(targetEntity = TissueMaterialImpl.class)
  @JoinColumn(name = "tissueMaterialId")
  private TissueMaterial tissueMaterial;

  @Enumerated(EnumType.STRING)
  private StrStatus strStatus = StrStatus.NOT_SUBMITTED;

  private String region;
  private String tubeId;

  @Override
  public SamplePurpose getSamplePurpose() {
    return samplePurpose;
  }

  @Override
  public void setSamplePurpose(SamplePurpose samplePurpose) {
    this.samplePurpose = samplePurpose;
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
    return "SampleAnalyteImpl [samplePurpose=" + samplePurpose + ", tissueMaterial="
        + tissueMaterial + ", strStatus=" + strStatus + ", region=" + region + ", tubeId=" + tubeId + "]";
  }

}
