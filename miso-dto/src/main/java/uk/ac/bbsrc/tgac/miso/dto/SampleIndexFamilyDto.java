package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;

public class SampleIndexFamilyDto {

  private Long id;
  private String name;
  private List<SampleIndexDto> indices;

  public static SampleIndexFamilyDto from(SampleIndexFamily from) {
    SampleIndexFamilyDto to = new SampleIndexFamilyDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
    to.setIndices(from.getIndices().stream().map(SampleIndexDto::from).toList());
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

  public List<SampleIndexDto> getIndices() {
    return indices;
  }

  public void setIndices(List<SampleIndexDto> indices) {
    this.indices = indices;
  }

  public SampleIndexFamily to() {
    SampleIndexFamily to = new SampleIndexFamily();
    setLong(to::setId, getId(), false);
    setString(to::setName, getName());
    to.setIndices(getIndices().stream().map(SampleIndexDto::to).toList());
    return to;
  }

}
