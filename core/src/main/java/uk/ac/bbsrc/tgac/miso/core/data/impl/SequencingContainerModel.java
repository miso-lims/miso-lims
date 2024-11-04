package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class SequencingContainerModel implements Aliasable, Deletable, Serializable, Barcodable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long sequencingContainerModelId = UNSAVED_ID;

  private String alias;

  private String identificationBarcode;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @ManyToMany(mappedBy = "containerModels")
  private List<InstrumentModel> instrumentModels;

  private int partitionCount;

  private boolean archived;

  private boolean fallback;

  @Override
  public long getId() {
    return sequencingContainerModelId;
  }

  @Override
  public void setId(long sequencingContainerModelId) {
    this.sequencingContainerModelId = sequencingContainerModelId;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public LocalDate getBarcodeDate() {
    return null;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public List<InstrumentModel> getInstrumentModels() {
    if (instrumentModels == null) {
      instrumentModels = new ArrayList<>();
    }
    return instrumentModels;
  }

  public void setInstrumentModels(List<InstrumentModel> instrumentModels) {
    this.instrumentModels = instrumentModels;
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

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Sequencing Container Model";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias() + " (" + getPlatformType().getKey() + ")";
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitContainerModel(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, archived, fallback, identificationBarcode, partitionCount, platformType);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        SequencingContainerModel::getAlias,
        SequencingContainerModel::isArchived,
        SequencingContainerModel::isFallback,
        SequencingContainerModel::getIdentificationBarcode,
        SequencingContainerModel::getPartitionCount,
        SequencingContainerModel::getPlatformType);
  }

}
