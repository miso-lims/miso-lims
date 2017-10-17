package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleAliquot.CATEGORY_NAME)
public class SampleAliquotDto extends SampleStockDto {

  private Long samplePurposeId;
  private String samplePurposeUrl;
  @JsonIgnore
  private Long stockClassId;
  private Long parentAliquotClassId;

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

  public Long getStockClassId() {
    return stockClassId;
  }

  public void setStockClassId(Long stockClassId) {
    this.stockClassId = stockClassId;
  }

  public Long getParentAliquotClassId() {
    return parentAliquotClassId;
  }

  public void setParentAliquotClassId(Long parentAliquotClassId) {
    this.parentAliquotClassId = parentAliquotClassId;
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
