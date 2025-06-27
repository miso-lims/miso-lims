package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;

public class DeliverableCategoryDto {

  private Long id;
  private String name;

  public static DeliverableCategoryDto from(DeliverableCategory from) {
    DeliverableCategoryDto to = new DeliverableCategoryDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
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

  public DeliverableCategory to() {
    DeliverableCategory to = new DeliverableCategory();
    setLong(to::setId, id, false);
    setString(to::setName, name);
    return to;
  }

}
