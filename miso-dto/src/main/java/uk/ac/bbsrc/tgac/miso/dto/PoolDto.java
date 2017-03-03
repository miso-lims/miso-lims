package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;
import java.util.Set;

import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class PoolDto {

  private Long id;
  private String url;
  private String name;
  private String alias;
  private Double concentration;
  private String identificationBarcode;
  private String locationLabel;
  private Boolean readyToRun;
  private Boolean qcPassed;
  private Integer avgInsertSize;
  private Set<DilutionDto> pooledElements;
  private String creationDate;
  private String lastModified;
  private String description;
  private Long boxId;


  public String getAlias() {
    return alias;
  }

  public Integer getAvgInsertSize() {
    return avgInsertSize;
  }

  public Long getBoxId() {
    return boxId;
  }

  public Double getConcentration() {
    return concentration;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public String getDescription() {
    return description;
  }

  public Long getId() {
    return id;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public String getLastModified() {
    return lastModified;
  }

  public String getLocationLabel() {
    return locationLabel;
  }

  public String getName() {
    return name;
  }

  public Set<DilutionDto> getPooledElements() {
    return pooledElements;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public Boolean getReadyToRun() {
    return readyToRun;
  }

  public String getUrl() {
    return url;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setAvgInsertSize(Integer avgInsertSize) {
    this.avgInsertSize = avgInsertSize;
  }

  public void setBoxId(Long boxId) {
    this.boxId = boxId;
  }

  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setLocationLabel(String locationLabel) {
    this.locationLabel = locationLabel;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPooledElements(Set<DilutionDto> pooledElements) {
    this.pooledElements = pooledElements;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public void setReadyToRun(Boolean readyToRun) {
    this.readyToRun = readyToRun;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void writeUrls(URI baseUri) {
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/pool/{id}").buildAndExpand(getId()).toUriString());
    for (DilutionDto ldto : getPooledElements()) {
      ldto.setLibraryUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/library/{id}").buildAndExpand(ldto.getLibrary().getId()).toUriString());
    }
  }

  public void writeUrls(UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    writeUrls(baseUri);
  }
}
