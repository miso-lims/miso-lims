package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = "Detailed")
public class DetailedLibraryDto extends LibraryDto {

  private Long kitDescriptorId;
  private Boolean archived;
  private Long libraryDesignId;
  private Long libraryDesignCodeId;
  private boolean nonStandardAlias;
  private Long preMigrationId;

  public Long getKitDescriptorId() {
    return kitDescriptorId;
  }

  public void setKitDescriptorId(Long kitDescriptorId) {
    this.kitDescriptorId = kitDescriptorId;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Long getLibraryDesignId() {
    return libraryDesignId;
  }

  public void setLibraryDesignId(Long libraryDesignId) {
    this.libraryDesignId = libraryDesignId;
  }

  public Long getLibraryDesignCodeId() {
    return libraryDesignCodeId;
  }

  public void setLibraryDesignCodeId(Long libraryDesignCodeId) {
    this.libraryDesignCodeId = libraryDesignCodeId;
  }

  public boolean getNonStandardAlias() {
    return nonStandardAlias;
  }

  public void setNonStandardAlias(boolean nonStandardAlias) {
    this.nonStandardAlias = nonStandardAlias;
  }

  public Long getPreMigrationId() {
    return preMigrationId;
  }

  public void setPreMigrationId(Long preMigrationId) {
    this.preMigrationId = preMigrationId;
  }

}
