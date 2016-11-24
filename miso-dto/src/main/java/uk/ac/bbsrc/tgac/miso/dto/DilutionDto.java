package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

public class DilutionDto {

  private Long id;
  private String name;
  private String identificationBarcode;
  private String locationLabel;
  private Double concentration;
  private String creationDate;
  private String dilutionCreatorName;
  private Long targetedSequencingId;
  private LibraryDto library;
  private String libraryUrl;

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

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

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

  public Double getConcentration() {
    return concentration;
  }

  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getDilutionUserName() {
    return dilutionCreatorName;
  }

  public void setDilutionUserName(String userName) {
    this.dilutionCreatorName = userName;
  }

  public Long getTargetedSequencingId() {
    return targetedSequencingId;
  }

  public void setTargetedSequencingId(Long targetedSequencingId) {
    this.targetedSequencingId = targetedSequencingId;
  }

  public LibraryDto getLibrary() {
    return library;
  }

  public void setLibrary(LibraryDto library) {
    this.library = library;
  }

  public String getLibraryUrl() {
    return libraryUrl;
  }

  public void setLibraryUrl(String libraryUrl) {
    this.libraryUrl = libraryUrl;
  }

}
