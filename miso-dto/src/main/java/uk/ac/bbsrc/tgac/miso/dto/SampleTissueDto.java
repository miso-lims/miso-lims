package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = SampleTissue.CATEGORY_NAME)
public class SampleTissueDto extends SampleIdentityDto {
  private String externalInstituteIdentifier;
  private Long labId;
  private String labUrl;
  private Integer passageNumber;
  private String region;
  private Integer timesReceived;
  private Long tissueMaterialId;
  private String tissueMaterialUrl;
  private Long tissueOriginId;
  private String tissueOriginUrl;
  private Long tissueTypeId;
  private String tissueTypeUrl;
  private Integer tubeNumber;

  public String getExternalInstituteIdentifier() {
    return externalInstituteIdentifier;
  }

  public Long getLabId() {
    return labId;
  }

  public String getLabUrl() {
    return labUrl;
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

  public String getTissueMaterialUrl() {
    return tissueMaterialUrl;
  }

  public Long getTissueOriginId() {
    return tissueOriginId;
  }

  public String getTissueOriginUrl() {
    return tissueOriginUrl;
  }

  public Long getTissueTypeId() {
    return tissueTypeId;
  }

  public String getTissueTypeUrl() {
    return tissueTypeUrl;
  }

  public Integer getTubeNumber() {
    return tubeNumber;
  }

  public void setExternalInstituteIdentifier(String externalInstituteIdentifier) {
    this.externalInstituteIdentifier = externalInstituteIdentifier;
  }

  public void setLabId(Long labId) {
    this.labId = labId;
  }

  public void setLabUrl(String labUrl) {
    this.labUrl = labUrl;
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

  public void setTissueMaterialUrl(String tissueMaterialUrl) {
    this.tissueMaterialUrl = tissueMaterialUrl;
  }

  public void setTissueOriginId(Long tissueOriginId) {
    this.tissueOriginId = tissueOriginId;
  }

  public void setTissueOriginUrl(String tissueOriginUrl) {
    this.tissueOriginUrl = tissueOriginUrl;
  }

  public void setTissueTypeId(Long tissueTypeId) {
    this.tissueTypeId = tissueTypeId;
  }

  public void setTissueTypeUrl(String tissueTypeUrl) {
    this.tissueTypeUrl = tissueTypeUrl;
  }

  public void setTubeNumber(Integer tubeNumber) {
    this.tubeNumber = tubeNumber;
  }

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    if (getTissueOriginId() != null) {
      setTissueOriginUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/tissueorigin/{id}").buildAndExpand(getTissueOriginId()).toUriString());
    }
    if (getTissueTypeId() != null) {
      setTissueTypeUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/tissuetype/{id}").buildAndExpand(getTissueTypeId()).toUriString());
    }
    if (getTissueMaterialId() != null) {
      setTissueMaterialUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/tissuematerial/{id}").buildAndExpand(getTissueMaterialId()).toUriString());
    }
    if (getLabId() != null) {
      setLabUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/lab/{id}").buildAndExpand(getLabId()).toUriString());
    }
  }

}
