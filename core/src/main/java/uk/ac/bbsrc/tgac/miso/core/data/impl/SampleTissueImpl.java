package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;

@Entity
@Table(name = "SampleTissue")
public class SampleTissueImpl extends SampleAdditionalInfoImpl implements SampleTissue {

  private static final long serialVersionUID = 1L;

  @OneToOne(targetEntity = TissueOriginImpl.class)
  @JoinColumn(name = "tissueOriginId")
  private TissueOrigin tissueOrigin;

  @OneToOne(targetEntity = TissueTypeImpl.class)
  @JoinColumn(name = "tissueTypeId")
  private TissueType tissueType;

  private Integer passageNumber;
  private Integer timesReceived;
  private Integer tubeNumber;
  private String externalInstituteIdentifier;

  @OneToOne(targetEntity = LabImpl.class)
  @JoinColumn(name = "labId", nullable = true)
  private Lab lab;

  @Override
  public TissueOrigin getTissueOrigin() {
    return tissueOrigin;
  }

  @Override
  public void setTissueOrigin(TissueOrigin tissueOrigin) {
    this.tissueOrigin = tissueOrigin;
  }

  @Override
  public TissueType getTissueType() {
    return tissueType;
  }

  @Override
  public void setTissueType(TissueType tissueType) {
    this.tissueType = tissueType;
  }

  @Override
  public Integer getPassageNumber() {
    return passageNumber;
  }

  @Override
  public void setPassageNumber(Integer passageNumber) {
    this.passageNumber = passageNumber;
  }

  @Override
  public Integer getTimesReceived() {
    return timesReceived;
  }

  @Override
  public void setTimesReceived(Integer timesReceived) {
    this.timesReceived = timesReceived;
  }

  @Override
  public Integer getTubeNumber() {
    return tubeNumber;
  }

  @Override
  public void setTubeNumber(Integer tubeNumber) {
    this.tubeNumber = tubeNumber;
  }

  @Override
  public String getExternalInstituteIdentifier() {
    return externalInstituteIdentifier;
  }

  @Override
  public void setExternalInstituteIdentifier(String externalInstituteIdentifier) {
    this.externalInstituteIdentifier = externalInstituteIdentifier;
  }

  @Override
  public Lab getLab() {
    return lab;
  }

  @Override
  public void setLab(Lab lab) {
    this.lab = lab;
  }

  private Integer cellularity;

  @Override
  public Integer getCellularity() {
    return cellularity;
  }

  @Override
  public void setCellularity(Integer cellularity) {
    this.cellularity = cellularity;
  }

  @Override
  public String toString() {
    return "SampleTissueImpl [cellularity=" + cellularity + "]";
  }

}
