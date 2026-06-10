package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetSection;

public class SampleSheetSectionDto {

  private String name;
  private Boolean optional;

  public static SampleSheetSectionDto from(SampleSheetSection from) {
    SampleSheetSectionDto to = new SampleSheetSectionDto();
    setString(to::setName, from.getName());
    setBoolean(to::setOptional, from.getOptional(), true);
    return to;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getOptional() {
    return optional;
  }

  public void setOptional(Boolean optional) {
    this.optional = optional;
  }


}
