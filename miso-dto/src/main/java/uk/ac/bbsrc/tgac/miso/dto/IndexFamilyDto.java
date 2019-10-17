package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class IndexFamilyDto {
  private boolean archived;
  private Long id;
  private List<IndexDto> indices;
  private String name;
  private String platformType;
  private boolean fakeSequence;
  private boolean uniqueDualIndex;

  public Long getId() {
    return id;
  }

  public List<IndexDto> getIndices() {
    return indices;
  }

  public String getName() {
    return name;
  }

  public String getPlatformType() {
    return platformType;
  }

  public boolean getFakeSequence() {
    return fakeSequence;
  }

  public boolean isArchived() {
    return archived;
  }

  public boolean isUniqueDualIndex() {
    return uniqueDualIndex;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setIndices(List<IndexDto> indices) {
    this.indices = indices;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public void setFakeSequence(boolean fake) {
    this.fakeSequence = fake;
  }

  public void setUniqueDualIndex(boolean uniqueDualIndex) {
    this.uniqueDualIndex = uniqueDualIndex;
  }
}
