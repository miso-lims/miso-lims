package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = SampleTissue.CATEGORY_NAME)
public class SampleTissueDto extends SampleIdentityDto {

  private Integer passageNumber;
  private Integer timesReceived;
  private Integer tubeNumber;
  private Long tissueOriginId;
  private String tissueOriginUrl;
  private Long tissueTypeId;
  private String tissueTypeUrl;
  private String externalInstituteIdentifier;
  private Long labId;
  private Integer cellularity;

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sample/tissue/{id}").buildAndExpand(getId()).toUriString());
    if (getTissueOriginId() != null) {
      setTissueOriginUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/tissueorigin/{id}").buildAndExpand(getTissueOriginId()).toUriString());
    }
    if (getTissueTypeId() != null) {
      setTissueTypeUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/tissuetype/{id}").buildAndExpand(getTissueTypeId()).toUriString());
    }
  }

  public Long getTissueOriginId() {
    return tissueOriginId;
  }

  public void setTissueOriginId(Long tissueOriginId) {
    this.tissueOriginId = tissueOriginId;
  }

  public String getTissueOriginUrl() {
    return tissueOriginUrl;
  }

  public void setTissueOriginUrl(String tissueOriginUrl) {
    this.tissueOriginUrl = tissueOriginUrl;
  }

  public Long getTissueTypeId() {
    return tissueTypeId;
  }

  public void setTissueTypeId(Long tissueTypeId) {
    this.tissueTypeId = tissueTypeId;
  }

  public String getTissueTypeUrl() {
    return tissueTypeUrl;
  }

  public void setTissueTypeUrl(String tissueTypeUrl) {
    this.tissueTypeUrl = tissueTypeUrl;
  }

  public Integer getPassageNumber() {
    return passageNumber;
  }

  public void setPassageNumber(Integer passageNumber) {
    this.passageNumber = passageNumber;
  }

  public Integer getTimesReceived() {
    return timesReceived;
  }

  public void setTimesReceived(Integer timesReceived) {
    this.timesReceived = timesReceived;
  }

  public Integer getTubeNumber() {
    return tubeNumber;
  }

  public void setTubeNumber(Integer tubeNumber) {
    this.tubeNumber = tubeNumber;
  }

  public String getExternalInstituteIdentifier() {
    return externalInstituteIdentifier;
  }

  public void setExternalInstituteIdentifier(String externalInstituteIdentifier) {
    this.externalInstituteIdentifier = externalInstituteIdentifier;
  }

  public Long getLabId() {
    return labId;
  }

  public void setLabId(Long labId) {
    this.labId = labId;
  }

  public Integer getCellularity() {
    return cellularity;
  }

  public void setCellularity(Integer cellularity) {
    this.cellularity = cellularity;
  }

  @Override
  public String toString() {
    return "SampleTissueDto [passageNumber=" + passageNumber
        + ", timesReceived=" + timesReceived + ", tubeNumber=" + tubeNumber
        + ", tissueOriginId=" + tissueOriginId + ", tissueOriginUrl="
        + tissueOriginUrl + ", tissueTypeId=" + tissueTypeId
        + ", tissueTypeUrl=" + tissueTypeUrl + ", externalInstituteIdentifier="
        + externalInstituteIdentifier + ", labId=" + labId + ", cellularity="
        + cellularity + ", super=" + super.toString() + "]";
  }

}
