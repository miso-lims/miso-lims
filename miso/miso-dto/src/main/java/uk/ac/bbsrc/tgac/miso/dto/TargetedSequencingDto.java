package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class TargetedSequencingDto {
  private String alias;
  private long id;
  private List<Long> kitDescriptorIds;

  public String getAlias() {
    return alias;
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

  public void setId(long id) {
    this.id = id;
  }

  public void setKitDescriptorIds(List<Long> kitDescriptorIds) {
    this.kitDescriptorIds = kitDescriptorIds;
  }
}
