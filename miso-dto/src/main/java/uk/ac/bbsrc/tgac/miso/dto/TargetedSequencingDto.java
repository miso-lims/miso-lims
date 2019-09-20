package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class TargetedSequencingDto {

  private long id;
  private String alias;
  private String description;
  private boolean archived;
  private List<Long> kitDescriptorIds;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean getArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public List<Long> getKitDescriptorIds() {
    return kitDescriptorIds;
  }

  public void setKitDescriptorIds(List<Long> kitDescriptorIds) {
    this.kitDescriptorIds = kitDescriptorIds;
  }
}
