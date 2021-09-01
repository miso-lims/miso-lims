package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.NucleicAcidType;
import uk.ac.bbsrc.tgac.miso.core.data.type.ThresholdType;

public class MetricDto {

  private static final String SEPARATOR = ", ";

  private Long id;
  private String alias;
  private String category;
  private Long subcategoryId;
  private String thresholdType;
  private String units;
  private Integer sortPriority;
  private String nucleicAcidType;
  private Long tissueMaterialId;
  private Long tissueTypeId;
  private boolean negateTissueType;
  private Long tissueOriginId;
  private Long containerModelId;
  private Integer readLength;
  private Integer readLength2;
  private String label;

  public static MetricDto from(Metric from) {
    MetricDto to = new MetricDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    setString(to::setCategory, maybeGetProperty(from.getCategory(), MetricCategory::name));
    Dtos.setId(to::setSubcategoryId, from.getSubcategory());
    setString(to::setThresholdType, maybeGetProperty(from.getThresholdType(), ThresholdType::name));
    setString(to::setUnits, from.getUnits());
    setInteger(to::setSortPriority, from.getSortPriority(), true);
    setString(to::setNucleicAcidType, maybeGetProperty(from.getNucleicAcidType(), NucleicAcidType::name));
    Dtos.setId(to::setTissueMaterialId, from.getTissueMaterial());
    Dtos.setId(to::setTissueTypeId, from.getTissueType());
    setBoolean(to::setNegateTissueType, from.isNegateTissueType(), false);
    Dtos.setId(to::setTissueOriginId, from.getTissueOrigin());
    Dtos.setId(to::setContainerModelId, from.getContainerModel());
    setInteger(to::setReadLength, from.getReadLength(), true);
    setInteger(to::setReadLength2, from.getReadLength2(), true);
    to.setLabel(makeLabel(from));
    return to;
  }

  private static String makeLabel(Metric from) {
    StringBuilder sb = new StringBuilder();

    if (from.getNucleicAcidType() != null) {
      sb.append(from.getNucleicAcidType()).append(SEPARATOR);
    }
    if (from.getTissueMaterial() != null) {
      sb.append(from.getTissueMaterial().getAlias()).append(SEPARATOR);
    }
    if (from.getTissueType() != null) {
      if (from.isNegateTissueType()) {
        sb.append("NOT ");
      }
      sb.append("tissue type ").append(from.getTissueType().getAlias()).append(SEPARATOR);
    }
    if (from.getTissueOrigin() != null) {
      sb.append(from.getTissueOrigin().getDescription()).append(SEPARATOR);
    }
    if (from.getContainerModel() != null) {
      sb.append(from.getContainerModel().getAlias()).append(SEPARATOR);
    }
    if (from.getReadLength() != null) {
      if (from.getReadLength2() != null) {
        if (from.getReadLength2().equals(from.getReadLength())) {
          sb.append("2×").append(from.getReadLength());
        } else {
          sb.append("Read lengths (").append(from.getReadLength()).append(", ").append(from.getReadLength2()).append(")");
        }
      } else {
        sb.append("1×").append(from.getReadLength());
      }
      sb.append(SEPARATOR);
    }

    if (sb.length() > 0) {
      sb.insert(0, " - ").delete(sb.length() - 2, sb.length());
    }
    sb.insert(0, from.getAlias());
    return sb.toString();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Long getSubcategoryId() {
    return subcategoryId;
  }

  public void setSubcategoryId(Long subcategoryId) {
    this.subcategoryId = subcategoryId;
  }

  public String getThresholdType() {
    return thresholdType;
  }

  public void setThresholdType(String thresholdType) {
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

  public String getNucleicAcidType() {
    return nucleicAcidType;
  }

  public void setNucleicAcidType(String nucleicAcidType) {
    this.nucleicAcidType = nucleicAcidType;
  }

  public Long getTissueMaterialId() {
    return tissueMaterialId;
  }

  public void setTissueMaterialId(Long tissueMaterialId) {
    this.tissueMaterialId = tissueMaterialId;
  }

  public Long getTissueTypeId() {
    return tissueTypeId;
  }

  public void setTissueTypeId(Long tissueTypeId) {
    this.tissueTypeId = tissueTypeId;
  }

  public boolean isNegateTissueType() {
    return negateTissueType;
  }

  public void setNegateTissueType(boolean negateTissueType) {
    this.negateTissueType = negateTissueType;
  }

  public Long getTissueOriginId() {
    return tissueOriginId;
  }

  public void setTissueOriginId(Long tissueOriginId) {
    this.tissueOriginId = tissueOriginId;
  }

  public Long getContainerModelId() {
    return containerModelId;
  }

  public void setContainerModelId(Long containerModelId) {
    this.containerModelId = containerModelId;
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

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Metric to() {
    Metric to = new Metric();
    setLong(to::setId, getId(), false);
    setString(to::setAlias, getAlias());
    setObject(to::setCategory, getCategory(), MetricCategory::valueOf);
    setObject(to::setSubcategory, MetricSubcategory::new, getSubcategoryId());
    setObject(to::setThresholdType, getThresholdType(), ThresholdType::valueOf);
    setString(to::setUnits, getUnits());
    setInteger(to::setSortPriority, getSortPriority(), true);
    setObject(to::setNucleicAcidType, getNucleicAcidType(), NucleicAcidType::valueOf);
    setObject(to::setTissueMaterial, TissueMaterialImpl::new, getTissueMaterialId());
    setObject(to::setTissueType, TissueTypeImpl::new, getTissueTypeId());
    setBoolean(to::setNegateTissueType, isNegateTissueType(), false);
    setObject(to::setTissueOrigin, TissueOriginImpl::new, getTissueOriginId());
    setObject(to::setContainerModel, SequencingContainerModel::new, getContainerModelId());
    setInteger(to::setReadLength, getReadLength(), true);
    setInteger(to::setReadLength2, getReadLength2(), true);
    return to;
  }

}
