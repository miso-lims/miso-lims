package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;

public class SampleIndexDto {

  private Long id;
  private Long indexFamilyId;
  private String name;

  public static SampleIndexDto from(SampleIndex from) {
    SampleIndexDto to = new SampleIndexDto();
    setLong(to::setId, from.getId(), true);
    Dtos.setId(to::setIndexFamilyId, from.getFamily());
    setString(to::setName, from.getName());
    return to;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getIndexFamilyId() {
    return indexFamilyId;
  }

  public void setIndexFamilyId(Long indexFamilyId) {
    this.indexFamilyId = indexFamilyId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SampleIndex to() {
    SampleIndex to = new SampleIndex();
    setLong(to::setId, getId(), false);
    setObject(to::setFamily, SampleIndexFamily::new, getIndexFamilyId());
    setString(to::setName, getName());
    return to;
  }

}
