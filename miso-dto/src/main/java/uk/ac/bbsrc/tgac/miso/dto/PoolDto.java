package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;
import java.util.Date;
import java.util.Set;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
public class PoolDto {

  private Long id;
  private String url;
  private String name;
  private String alias;
  private Double concentration;
  private String identificationBarcode;
  private Boolean readyToRun;
  private Boolean qcPassed;
  private Integer avgInsertSize;
  private Set<DilutionDto> pooledElements;
  private Date creationDate;
  private String lastModified;

  public String getAlias() {
    return alias;
  }

  public Integer getAvgInsertSize() {
    return avgInsertSize;
  }

  public Double getConcentration() {
    return concentration;
  }

  public Date getCreationDate() {
    return creationDate;
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

  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
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

  public void writeUrls(UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    writeUrls(baseUri);
  }

  public void writeUrls(URI baseUri) {
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/pool/{id}").buildAndExpand(getId()).toUriString());
    for (DilutionDto ldto : getPooledElements()) {
      ldto.setLibraryUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/library/{id}").buildAndExpand(ldto.getLibrary().getId()).toUriString());
    }
  }
}
