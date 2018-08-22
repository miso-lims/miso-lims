package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonInclude;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

public class DilutionDto extends AbstractBoxableDto implements WritableUrls {

  private Long id;
  private String name;
  private String identificationBarcode;
  private String locationLabel;
  private String concentration;
  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;
  private String volume;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;
  private String creationDate;
  private String dilutionCreatorName;
  private Long targetedSequencingId;
  private LibraryDto library;
  private String libraryUrl;
  private String lastModified;
  private List<Long> indexIds;
  private String identityConsentLevel;
  private String ngUsed;
  private String volumeUsed;
  private Integer proportion;

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

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationLabel() {
    return locationLabel;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setLocationLabel(String locationLabel) {
    this.locationLabel = locationLabel;
  }

  public String getConcentration() {
    return concentration;
  }

  public void setConcentration(String concentration) {
    this.concentration = concentration;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
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

  @Override
  public void writeUrls(URI baseUri) {
    if (library != null) {
      library.writeUrls(baseUri);
    }
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public List<Long> getIndexIds() {
    return indexIds;
  }

  public void setIndexIds(List<Long> indexIds) {
    this.indexIds = indexIds;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  public void setVolumeUnits(VolumeUnit volumeUnit) {
    this.volumeUnits = volumeUnit;
  }

  public String getIdentityConsentLevel() {
    return identityConsentLevel;
  }

  public void setIdentityConsentLevel(String identityConsentLevel) {
    this.identityConsentLevel = identityConsentLevel;
  }

  public String getNgUsed() {
    return ngUsed;
  }

  public void setNgUsed(String ngUsed) {
    this.ngUsed = ngUsed;
  }

  public String getVolumeUsed() {
    return volumeUsed;
  }

  public void setVolumeUsed(String volumeUsed) {
    this.volumeUsed = volumeUsed;
  }

  public Integer getProportion() {
    return proportion;
  }

  public void setProportion(Integer proportion) {
    this.proportion = proportion;
  }

}
