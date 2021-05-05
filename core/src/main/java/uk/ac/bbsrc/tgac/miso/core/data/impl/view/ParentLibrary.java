package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Immutable
@Table(name = "Library")
public class ParentLibrary implements IndexedLibrary, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long libraryId;

  private String name;
  private String alias;
  private String description;
  private boolean lowQuality;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @ManyToOne
  @JoinColumn(name = "index1Id")
  private Index index1;

  @ManyToOne
  @JoinColumn(name = "index2Id")
  private Index index2;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;

  @ManyToOne
  @JoinColumn(name = "sample_sampleId")
  private ParentSample parentSample;

  public long getId() {
    return libraryId;
  }

  public void setId(long id) {
    this.libraryId = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public boolean isLowQuality() {
    return lowQuality;
  }

  public void setLowQuality(boolean lowQuality) {
    this.lowQuality = lowQuality;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  @Override
  public Index getIndex1() {
    return index1;
  }

  @Override
  public void setIndex1(Index index1) {
    this.index1 = index1;
  }

  @Override
  public Index getIndex2() {
    return index2;
  }

  @Override
  public void setIndex2(Index index2) {
    this.index2 = index2;
  }

  public DetailedQcStatus getDetailedQcStatus() {
    return detailedQcStatus;
  }

  public void setDetailedQcStatus(DetailedQcStatus detailedQcStatus) {
    this.detailedQcStatus = detailedQcStatus;
  }

  public ParentSample getParentSample() {
    return parentSample;
  }

  public void setParentSample(ParentSample parentSample) {
    this.parentSample = parentSample;
  }

}
