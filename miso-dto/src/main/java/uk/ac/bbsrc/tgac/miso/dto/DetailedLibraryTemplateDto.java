package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonTypeName(value = "Detailed")
public class DetailedLibraryTemplateDto extends LibraryTemplateDto {

  private Long designId;
  private Long designCodeId;

  public Long getDesignId() {
    return designId;
  }

  public void setDesignId(Long designId) {
    this.designId = designId;
  }

  public Long getDesignCodeId() {
    return designCodeId;
  }

  public void setDesignCodeId(Long designCodeId) {
    this.designCodeId = designCodeId;
  }

}
