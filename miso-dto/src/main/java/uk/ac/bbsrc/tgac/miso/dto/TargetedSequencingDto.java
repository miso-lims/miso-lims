package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class TargetedSequencingDto {
  private String alias;
  private boolean archived;
  private long id;
  private List<Long> kitDescriptorIds;

  public String getAlias() {
    return alias;
  }

  public boolean getArchived() {
    return archived;
  }

  public long getId() {
    return id;
  }

  public List<Long> getKitDescriptorIds() {
    return kitDescriptorIds;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setKitDescriptorIds(List<Long> kitDescriptorIds) {
    this.kitDescriptorIds = kitDescriptorIds;
  }
}
