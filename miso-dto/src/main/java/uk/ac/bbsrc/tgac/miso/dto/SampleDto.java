package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = SampleAliquotDto.class, name = SampleAliquot.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleIdentityDto.class, name = SampleIdentity.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleStockDto.class, name = SampleStock.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleTissueDto.class, name = SampleTissue.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleTissueProcessingDto.class, name = SampleTissueProcessing.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleSlideDto.class, name = SampleSlide.SAMPLE_CLASS_NAME),
    @JsonSubTypes.Type(value = SampleLCMTubeDto.class, name = SampleLCMTube.SAMPLE_CLASS_NAME),
    @JsonSubTypes.Type(value = SampleDto.class, name = "Plain") })
@JsonTypeName(value = "Plain")
public class SampleDto extends AbstractBoxableDto implements WritableUrls {

  private Long id;
  private String url;

  private String accession;
  private String name;
  private String description;
  // Skipped security profile
  private String identificationBarcode;
  private String locationBarcode;
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
  private String volume;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;
  private String concentration;
  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;
  private Long updatedById;
  private String updatedByUrl;
  private String lastModified;
  private String qcDv200;
  private String qcRin;
  private List<QcDto> qcs;

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

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  public String getLocationLabel() {
    return locationLabel;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
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

  @JsonInclude(JsonInclude.Include.ALWAYS)
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

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public String getConcentration() {
    return concentration;
  }

  public void setConcentration(String concentration) {
    this.concentration = concentration;
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

  public String getQcDv200() {
    return qcDv200;
  }

  public void setQcDv200(String qcDv200) {
    this.qcDv200 = qcDv200;
  }

  public String getQcRin() {
    return qcRin;
  }

  public void setQcRin(String qcRin) {
    this.qcRin = qcRin;
  }

  public List<QcDto> getQcs() {
    return qcs;
  }

  public void setQcs(List<QcDto> qcs) {
    this.qcs = qcs;
  }

  @Override
  public void writeUrls(URI baseUri) {
    setUrl(WritableUrls.buildUriPath(baseUri, "/rest/sample/{id}", getId()));
    setUpdatedByUrl(WritableUrls.buildUriPath(baseUri, "/rest/user/{id}", getUpdatedById()));
    if (getRootSampleClassId() != null) {
      setRootSampleClassUrl(WritableUrls.buildUriPath(baseUri, "/rest/sampleclass/{id}", getRootSampleClassId()));
    }
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

}
