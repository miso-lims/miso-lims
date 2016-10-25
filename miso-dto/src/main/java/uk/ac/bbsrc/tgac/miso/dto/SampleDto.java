package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleCVSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = SampleAliquotDto.class, name = SampleAliquot.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleIdentityDto.class, name = Identity.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleStockDto.class, name = SampleStock.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleTissueDto.class, name = SampleTissue.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleTissueProcessingDto.class, name = SampleTissueProcessing.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleCVSlideDto.class, name = SampleCVSlide.SAMPLE_CLASS_NAME),
    @JsonSubTypes.Type(value = SampleLCMTubeDto.class, name = SampleLCMTube.SAMPLE_CLASS_NAME),
    @JsonSubTypes.Type(value = SampleDto.class, name = "Plain") })
@JsonTypeName(value = "Plain")
public class SampleDto {

  private Long id;
  private String url;

  private String accession;
  private String name;
  private String description;
  // Skipped security profile
  private String identificationBarcode;
  private String locationLabel;
  private String sampleType;
  private String receivedDate;
  private Boolean qcPassed;
  private String alias;
  private Long projectId;
  private String scientificName;
  private String taxonIdentifier;
  private Long rootSampleClassId;
  private String rootSampleClassUrl;
  private Double volume;
  private Boolean discarded;
  private Long updatedById;
  private String updatedByUrl;
  private String lastModified;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationLabel() {
    return locationLabel;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setLocationLabel(String locationLabel) {
    this.locationLabel = locationLabel;
  }

  public String getSampleType() {
    return sampleType;
  }

  public void setSampleType(String sampleType) {
    this.sampleType = sampleType;
  }

  public String getReceivedDate() {
    return receivedDate;
  }

  public void setReceivedDate(String receivedDate) {
    this.receivedDate = receivedDate;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public String getScientificName() {
    return scientificName;
  }

  public void setScientificName(String scientificName) {
    this.scientificName = scientificName;
  }

  public String getTaxonIdentifier() {
    return taxonIdentifier;
  }

  public void setTaxonIdentifier(String taxonIdentifier) {
    this.taxonIdentifier = taxonIdentifier;
  }

  public Long getRootSampleClassId() {
    return rootSampleClassId;
  }

  public void setRootSampleClassId(Long rootSampleClassId) {
    this.rootSampleClassId = rootSampleClassId;
  }

  public String getRootSampleClassUrl() {
    return rootSampleClassUrl;
  }

  public void setRootSampleClassUrl(String rootSampleClassUrl) {
    this.rootSampleClassUrl = rootSampleClassUrl;
  }

  public Double getVolume() {
    return volume;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public Boolean getDiscarded() {
    return discarded;
  }

  public void setDiscarded(Boolean discarded) {
    this.discarded = discarded;
  }

  public Long getUpdatedById() {
    return updatedById;
  }

  public void setUpdatedById(Long updatedById) {
    this.updatedById = updatedById;
  }

  public String getUpdatedByUrl() {
    return updatedByUrl;
  }

  public void setUpdatedByUrl(String updatedByUrl) {
    this.updatedByUrl = updatedByUrl;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public void writeUrls(UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    writeUrls(baseUri);
  }

  public void writeUrls(URI baseUri) {
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/tree/sample/{id}").buildAndExpand(getId()).toUriString());
    setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(getUpdatedById()).toUriString());
    if (getRootSampleClassId() != null) {
      setRootSampleClassUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/sampleclass/{id}").buildAndExpand(getRootSampleClassId()).toUriString());
    }
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sample/{id}").buildAndExpand(getId()).toUriString());
  }

}
