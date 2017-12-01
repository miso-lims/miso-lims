package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class IndexFamilyDto {
  private boolean archived;
  private long id;
  private List<IndexDto> indices;
  private int maximumNumber;
  private String name;
  private String platformType;

  public long getId() {
    return id;
  }

  public List<IndexDto> getIndices() {
    return indices;
  }

  public int getMaximumNumber() {
    return maximumNumber;
  }

  public String getName() {
    return name;
  }

  public String getPlatformType() {
    return platformType;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setIndices(List<IndexDto> indices) {
    this.indices = indices;
  }

  public void setMaximumNumber(int maximumNumber) {
    this.maximumNumber = maximumNumber;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }
}
