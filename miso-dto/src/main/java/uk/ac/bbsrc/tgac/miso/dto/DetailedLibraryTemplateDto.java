package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = "Detailed")
public class DetailedLibraryTemplateDto extends LibraryTemplateDto {

  private long designId;

  private long designCodeId;

  public long getDesignId() {
    return designId;
  }

  public void setDesignId(long designId) {
    this.designId = designId;
  }

  public long getLibraryDesignCodeId() {
    return designCodeId;
  }

  public void setLibraryDesignCodeId(long designCodeId) {
    this.designCodeId = designCodeId;
  }

}
