package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetField;

public class SampleSheetFieldDto {

  private String name;
  private Boolean omitIfEmpty;
  private List<SampleSheetFieldSourceDto> sources;

  public static SampleSheetFieldDto from(SampleSheetField from) {
    SampleSheetFieldDto to = new SampleSheetFieldDto();
    setString(to::setName, from.getName());
    setBoolean(to::setOmitIfEmpty, from.getOmitIfEmpty(), true);
    setObject(to::setSources, from.getSources(), list -> list.stream().map(SampleSheetFieldSourceDto::from).toList());
    return to;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getOmitIfEmpty() {
    return omitIfEmpty;
  }

  public void setOmitIfEmpty(Boolean omitIfEmpty) {
    this.omitIfEmpty = omitIfEmpty;
  }

  public List<SampleSheetFieldSourceDto> getSources() {
    return sources;
  }

  public void setSources(List<SampleSheetFieldSourceDto> sources) {
    this.sources = sources;
  }

  public SampleSheetField to() {
    SampleSheetField to = new SampleSheetField();
    setString(to::setName, getName());
    setBoolean(to::setOmitIfEmpty, getOmitIfEmpty(), true);
    setObject(to::setSources, getSources(), list -> list.stream().map(SampleSheetFieldSourceDto::to).toList());
    return to;
  }

}
