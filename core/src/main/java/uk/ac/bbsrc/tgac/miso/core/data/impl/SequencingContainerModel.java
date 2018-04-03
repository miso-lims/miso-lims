package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
public class SequencingContainerModel {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long sequencingContainerModelId;

  private String alias;

  private String identificationBarcode;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @ManyToMany
  @JoinTable(name = "SequencingContainerModel_Platform",
      joinColumns = { @JoinColumn(name = "sequencingContainerModelId", nullable = false) },
      inverseJoinColumns = { @JoinColumn(name = "platformId", nullable = false) })
  private List<Platform> platforms;

  private int partitionCount;

  private boolean archived;

  private boolean fallback;

  public long getId() {
    return sequencingContainerModelId;
  }

  public void setId(long sequencingContainerModelId) {
    this.sequencingContainerModelId = sequencingContainerModelId;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public List<Platform> getPlatforms() {
    return platforms;
  }

  public void setPlatforms(List<Platform> platforms) {
    this.platforms = platforms;
  }

  public int getPartitionCount() {
    return partitionCount;
  }

  public void setPartitionCount(int partitionCount) {
    this.partitionCount = partitionCount;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  /**
   * @return true if this container may be used as a fallback when the exact model is unknown
   */
  public boolean isFallback() {
    return fallback;
  }

  public void setFallback(boolean fallback) {
    this.fallback = fallback;
  }

}
