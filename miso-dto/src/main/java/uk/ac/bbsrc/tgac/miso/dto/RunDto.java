package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

public class RunDto {
  private Long id;
  private String name;
  private String alias;
  private String status;
  private String lastModified;
  private String platformType;
  private String startDate;
  private String endDate;
  private String url;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastUpdated) {
    this.lastModified = lastUpdated;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void writeUrls(UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    writeUrls(baseUri);
  }

  public void writeUrls(URI baseUri) {
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/run/{id}").buildAndExpand(getId()).toUriString());
  }
}
