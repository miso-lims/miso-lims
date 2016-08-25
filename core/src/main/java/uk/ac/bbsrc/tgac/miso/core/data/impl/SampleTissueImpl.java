package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;

@Entity
@Table(name = "SampleTissue")
public class SampleTissueImpl extends SampleAdditionalInfoImpl implements SampleTissue {

  private String externalInstituteIdentifier;

  @OneToOne(targetEntity = LabImpl.class)
  @JoinColumn(name = "labId", nullable = true)
  private Lab lab;

  private Integer passageNumber;

  private String region;

  private Integer timesReceived;

  @OneToOne(targetEntity = TissueMaterialImpl.class)
  @JoinColumn(name = "tissueMaterialId")
  private TissueMaterial tissueMaterial;

  @OneToOne(targetEntity = TissueOriginImpl.class)
  @JoinColumn(name = "tissueOriginId")
  private TissueOrigin tissueOrigin;

  @OneToOne(targetEntity = TissueTypeImpl.class)
  @JoinColumn(name = "tissueTypeId")
  private TissueType tissueType;

  private Integer tubeNumber;

  @Override
  public String getExternalInstituteIdentifier() {
    return externalInstituteIdentifier;
  }

  @Override
  public Lab getLab() {
    return lab;
  }

  @Override
  public Integer getPassageNumber() {
    return passageNumber;
  }

  @Override
  public String getRegion() {
    return region;
  }

  @Override
  public Integer getTimesReceived() {
    return timesReceived;
  }

  @Override
  public TissueMaterial getTissueMaterial() {
    return tissueMaterial;
  }

  @Override
  public TissueOrigin getTissueOrigin() {
    return tissueOrigin;
  }

  @Override
  public TissueType getTissueType() {
    return tissueType;
  }

  @Override
  public Integer getTubeNumber() {
    return tubeNumber;
  }

  @Override
  public void setExternalInstituteIdentifier(String externalInstituteIdentifier) {
    this.externalInstituteIdentifier = nullifyStringIfBlank(externalInstituteIdentifier);
  }

  @Override
  public void setLab(Lab lab) {
    this.lab = lab;
  }

  @Override
  public void setPassageNumber(Integer passageNumber) {
    this.passageNumber = passageNumber;
  }

  @Override
  public void setRegion(String region) {
    this.region = nullifyStringIfBlank(region);
  }

  @Override
  public void setTimesReceived(Integer timesReceived) {
    this.timesReceived = timesReceived;
  }

  @Override
  public void setTissueMaterial(TissueMaterial tissueMaterial) {
    this.tissueMaterial = tissueMaterial;
  }

  @Override
  public void setTissueOrigin(TissueOrigin tissueOrigin) {
    this.tissueOrigin = tissueOrigin;
  }

  @Override
  public void setTissueType(TissueType tissueType) {
    this.tissueType = tissueType;
  }

  @Override
  public void setTubeNumber(Integer tubeNumber) {
    this.tubeNumber = tubeNumber;
  }
}
