package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class SampleSheetDto {

  private Long id;
  private String name;
  private String platformType;
  private List<SampleSheetParameterDto> parameters;
  private List<SampleSheetSectionDto> sections;

  public static SampleSheetDto from(SampleSheet from) {
    SampleSheetDto to = new SampleSheetDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
    setString(to::setPlatformType, maybeGetProperty(from.getPlatformType(), PlatformType::name));
    if (from.getParameters() != null) {
      to.setParameters(from.getParameters().stream().map(SampleSheetParameterDto::from).toList());
    }
    if (from.getSections() != null) {
      to.setSections(from.getSections().stream().map(SampleSheetSectionDto::from).toList());
    }
    return to;
  }

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

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public List<SampleSheetParameterDto> getParameters() {
    return parameters;
  }

  public void setParameters(List<SampleSheetParameterDto> parameters) {
    this.parameters = parameters;
  }

  public List<SampleSheetSectionDto> getSections() {
    return sections;
  }

  public void setSections(List<SampleSheetSectionDto> sections) {
    this.sections = sections;
  }

}
