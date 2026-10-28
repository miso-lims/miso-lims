package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;

public class SampleIndexDto {
  private SampleIndexFamilyDto family;
  private Long id;
  private Long indexFamilyId;
  private int position = 1;
  private String sequence;
  private String name;

  public SampleIndexFamilyDto getFamily() {
    return family;
  }

  public void setFamily(SampleIndexFamilyDto family) {
    this.family = family;
  }

  public static SampleIndexDto from(SampleIndex from) {
    SampleIndexDto dto = new SampleIndexDto();
    setLong(dto::setId, from.getId(), true);
    dto.setPosition(from.getPosition());
    dto.setSequence(from.getSequence());
    Dtos.setId(dto::setIndexFamilyId, from.getFamily());
    setString(dto::setName, from.getName());
    dto.setFamily(SampleIndexFamilyDto.from(from.getFamily(), false));
    return dto;
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

  public int getPosition() {
    return position;
  }

  public String getSequence() {
    return sequence;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public void setSequence(String sequence) {
    this.sequence = sequence;
  }


  public SampleIndex to() {
    SampleIndex to = new SampleIndex();
    setLong(to::setId, getId(), false);
    setObject(to::setFamily, SampleIndexFamily::new, getIndexFamilyId());
    setString(to::setName, getName());
    setString(to::setSequence, getSequence());
    setInteger(to::setPosition, getPosition(), false);
    return to;
  }

}
