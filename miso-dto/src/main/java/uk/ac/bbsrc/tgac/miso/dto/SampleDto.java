package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleDto {

  private Long id;
  private String url;

  private String accession;
  private String name;
  private String description;
  // Skipped security profile
  private String identificationBarcode;
  private String locationBarcode;
  private String sampleType;
  private String receivedDate;
  private Boolean qcPassed;
  private String alias;
  private Long projectId;
  private String scientificName;
  private String taxonIdentifier;
  private SampleIdentityDto sampleIdentityDto;
  private SampleAnalyteDto sampleAnalyte;
  private SampleAdditionalInfoDto sampleAdditionalInfo;

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

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
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

  public SampleIdentityDto getSampleIdentityDto() {
    return sampleIdentityDto;
  }

  public void setSampleIdentityDto(SampleIdentityDto sampleIdentityDto) {
    this.sampleIdentityDto = sampleIdentityDto;
  }

  public SampleAnalyteDto getSampleAnalyte() {
    return sampleAnalyte;
  }

  public void setSampleAnalyte(SampleAnalyteDto sampleAnalyte) {
    this.sampleAnalyte = sampleAnalyte;
  }

  public SampleAdditionalInfoDto getSampleAdditionalInfo() {
    return sampleAdditionalInfo;
  }

  public void setSampleAdditionalInfo(SampleAdditionalInfoDto sampleAdditionalInfo) {
    this.sampleAdditionalInfo = sampleAdditionalInfo;
  }

}
