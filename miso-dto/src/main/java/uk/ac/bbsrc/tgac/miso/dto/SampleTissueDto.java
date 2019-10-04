package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;

@JsonTypeName(value = SampleTissue.CATEGORY_NAME)
public class SampleTissueDto extends SampleIdentityDto {
  private String secondaryIdentifier;
  private Long labId;
  private Integer passageNumber;
  private String region;
  private Integer timesReceived;
  private Long tissueMaterialId;
  private Long tissueOriginId;
  private Long tissueTypeId;
  private Integer tubeNumber;

  public String getSecondaryIdentifier() {
    return secondaryIdentifier;
  }

  public Long getLabId() {
    return labId;
  }

  public Integer getPassageNumber() {
    return passageNumber;
  }

  public String getRegion() {
    return region;
  }

  public Integer getTimesReceived() {
    return timesReceived;
  }

  public Long getTissueMaterialId() {
    return tissueMaterialId;
  }

  public Long getTissueOriginId() {
    return tissueOriginId;
  }

  public Long getTissueTypeId() {
    return tissueTypeId;
  }

  public Integer getTubeNumber() {
    return tubeNumber;
  }

  public void setSecondaryIdentifier(String secondaryIdentifier) {
    this.secondaryIdentifier = secondaryIdentifier;
  }

  public void setLabId(Long labId) {
    this.labId = labId;
  }

  public void setPassageNumber(Integer passageNumber) {
    this.passageNumber = passageNumber;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public void setTimesReceived(Integer timesReceived) {
    this.timesReceived = timesReceived;
  }

  public void setTissueMaterialId(Long tissueMaterialId) {
    this.tissueMaterialId = tissueMaterialId;
  }

  public void setTissueOriginId(Long tissueOriginId) {
    this.tissueOriginId = tissueOriginId;
  }

  public void setTissueTypeId(Long tissueTypeId) {
    this.tissueTypeId = tissueTypeId;
  }

  public void setTubeNumber(Integer tubeNumber) {
    this.tubeNumber = tubeNumber;
  }

}
