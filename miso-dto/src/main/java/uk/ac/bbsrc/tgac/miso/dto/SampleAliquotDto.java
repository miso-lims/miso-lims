package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

public class SampleAliquotDto extends SampleStockDto {

  private Long samplePurposeId;
  private String samplePurposeUrl;

  public Long getSamplePurposeId() {
    return samplePurposeId;
  }

  public String getSamplePurposeUrl() {
    return samplePurposeUrl;
  }

  public void setSamplePurposeId(Long samplePurposeId) {
    this.samplePurposeId = samplePurposeId;
  }

  public void setSamplePurposeUrl(String samplePurposeUrl) {
    this.samplePurposeUrl = samplePurposeUrl;
  }

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    if (getSamplePurposeId() != null) {
      setSamplePurposeUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/samplepurpose/{id}").buildAndExpand(getSamplePurposeId()).toUriString());
    }
  }

}
