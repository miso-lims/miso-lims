package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLabel;

public class StorageLabelDto {

  public static StorageLabelDto from(StorageLabel from) {
    StorageLabelDto to = new StorageLabelDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setLabel, from.getLabel());
    return to;
  }

  private long id;
  private String label;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public StorageLabel to() {
    StorageLabel to = new StorageLabel();
    setLong(to::setId, getId(), false);
    setString(to::setLabel, getLabel());
    return to;
  }

}
