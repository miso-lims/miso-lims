package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.NucleicAcidType;
import uk.ac.bbsrc.tgac.miso.core.data.type.ThresholdType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Metric implements Aliasable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;
  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long metricId;

  private String alias;

  @Enumerated(EnumType.STRING)
  private MetricCategory category;

  @ManyToOne
  @JoinColumn(name = "subcategoryId")
  private MetricSubcategory subcategory;

  @Enumerated(EnumType.STRING)
  private ThresholdType thresholdType;

  private String units;

  private Integer sortPriority;

  @Enumerated(EnumType.STRING)
  private NucleicAcidType nucleicAcidType;

  @ManyToOne(targetEntity = TissueMaterialImpl.class)
  @JoinColumn(name = "tissueMaterialId")
  private TissueMaterial tissueMaterial;

  @ManyToOne(targetEntity = TissueTypeImpl.class)
  @JoinColumn(name = "tissueTypeId")
  private TissueType tissueType;

  private boolean negateTissueType = false;

  @ManyToOne(targetEntity = TissueOriginImpl.class)
  @JoinColumn(name = "tissueOriginId")
  private TissueOrigin tissueOrigin;

  @ManyToOne
  @JoinColumn(name = "containerModelId")
  private SequencingContainerModel containerModel;

  private Integer readLength;

  private Integer readLength2;

  @Override
  public long getId() {
    return metricId;
  }

  @Override
  public void setId(long id) {
    this.metricId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Metric";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public MetricCategory getCategory() {
    return category;
  }

  public void setCategory(MetricCategory category) {
    this.category = category;
  }

  public MetricSubcategory getSubcategory() {
    return subcategory;
  }

  public void setSubcategory(MetricSubcategory subcategory) {
    this.subcategory = subcategory;
  }

  public ThresholdType getThresholdType() {
    return thresholdType;
  }

  public void setThresholdType(ThresholdType thresholdType) {
    this.thresholdType = thresholdType;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public Integer getSortPriority() {
    return sortPriority;
  }

  public void setSortPriority(Integer sortPriority) {
    this.sortPriority = sortPriority;
  }

  public NucleicAcidType getNucleicAcidType() {
    return nucleicAcidType;
  }

  public void setNucleicAcidType(NucleicAcidType nucleicAcidType) {
    this.nucleicAcidType = nucleicAcidType;
  }

  public TissueMaterial getTissueMaterial() {
    return tissueMaterial;
  }

  public void setTissueMaterial(TissueMaterial tissueMaterial) {
    this.tissueMaterial = tissueMaterial;
  }

  public TissueType getTissueType() {
    return tissueType;
  }

  public void setTissueType(TissueType tissueType) {
    this.tissueType = tissueType;
  }

  public boolean isNegateTissueType() {
    return negateTissueType;
  }

  public void setNegateTissueType(boolean negateTissueType) {
    this.negateTissueType = negateTissueType;
  }

  public TissueOrigin getTissueOrigin() {
    return tissueOrigin;
  }

  public void setTissueOrigin(TissueOrigin tissueOrigin) {
    this.tissueOrigin = tissueOrigin;
  }

  public SequencingContainerModel getContainerModel() {
    return containerModel;
  }

  public void setContainerModel(SequencingContainerModel containerModel) {
    this.containerModel = containerModel;
  }

  public Integer getReadLength() {
    return readLength;
  }

  public void setReadLength(Integer readLength) {
    this.readLength = readLength;
  }

  public Integer getReadLength2() {
    return readLength2;
  }

  public void setReadLength2(Integer readLength2) {
    this.readLength2 = readLength2;
  }

  @Override
  public int hashCode() {
    return Objects.hash(metricId, alias, category, thresholdType, units, sortPriority, nucleicAcidType, tissueMaterial,
        tissueType,
        negateTissueType, tissueOrigin, containerModel, readLength, readLength2);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        Metric::getId,
        Metric::getAlias,
        Metric::getCategory,
        Metric::getThresholdType,
        Metric::getUnits,
        Metric::getSortPriority,
        Metric::getNucleicAcidType,
        Metric::getTissueMaterial,
        Metric::getTissueType,
        Metric::isNegateTissueType,
        Metric::getTissueOrigin,
        Metric::getContainerModel,
        Metric::getReadLength,
        Metric::getReadLength2);
  }

}
