package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class WorksetDto {

  private Long id;
  private String alias;
  private String description;
  private List<Long> sampleIds;
  private List<Long> libraryIds;
  private List<Long> libraryAliquotIds;
  private String creator;
  private String lastModified;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Long> getSampleIds() {
    return sampleIds;
  }

  public void setSampleIds(List<Long> sampleIds) {
    this.sampleIds = sampleIds;
  }

  public List<Long> getLibraryIds() {
    return libraryIds;
  }

  public void setLibraryIds(List<Long> libraryIds) {
    this.libraryIds = libraryIds;
  }

  public List<Long> getLibraryAliquotIds() {
    return libraryAliquotIds;
  }

  public void setLibraryAliquotIds(List<Long> libraryAliquotIds) {
    this.libraryAliquotIds = libraryAliquotIds;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

}
