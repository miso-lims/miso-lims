package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = SampleAnalyte.CATEGORY_NAME)
public class SampleAnalyteDto extends SampleTissueDto {

  private Long samplePurposeId;
  private String samplePurposeUrl;
  private Long tissueMaterialId;

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sample/analyte/{id}").buildAndExpand(getId()).toUriString());
    if (getSamplePurposeId() != null) {
      setSamplePurposeUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/samplepurpose/{id}").buildAndExpand(getSamplePurposeId()).toUriString());
    }
    if (getTissueMaterialId() != null) {
      setTissueMaterialUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/tissuematerial/{id}").buildAndExpand(getTissueMaterialId()).toUriString());
    }
  }

  private String tissueMaterialUrl;

  private String region;
  private String tubeId;
  private String strStatus;

  public Long getSamplePurposeId() {
    return samplePurposeId;
  }

  public void setSamplePurposeId(Long samplePurposeId) {
    this.samplePurposeId = samplePurposeId;
  }

  public String getSamplePurposeUrl() {
    return samplePurposeUrl;
  }

  public void setSamplePurposeUrl(String samplePurposeUrl) {
    this.samplePurposeUrl = samplePurposeUrl;
  }

  public String getTissueMaterialUrl() {
    return tissueMaterialUrl;
  }

  public void setTissueMaterialUrl(String tissueMaterialUrl) {
    this.tissueMaterialUrl = tissueMaterialUrl;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getTubeId() {
    return tubeId;
  }

  public void setTubeId(String tubeId) {
    this.tubeId = tubeId;
  }

  public Long getTissueMaterialId() {
    return tissueMaterialId;
  }

  public void setTissueMaterialId(Long tissueMaterialId) {
    this.tissueMaterialId = tissueMaterialId;
  }

  public String getStrStatus() {
    return strStatus;
  }

  public void setStrStatus(String strStatus) {
    this.strStatus = strStatus;
  }

}
