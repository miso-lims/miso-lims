package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.ThresholdType;

public class MetricDto {

  private Long id;
  private String alias;
  private String category;
  private Long subcategoryId;
  private String thresholdType;
  private String units;
  private Integer sortPriority;

  public static MetricDto from(Metric from) {
    MetricDto to = new MetricDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    setString(to::setCategory, maybeGetProperty(from.getCategory(), MetricCategory::name));
    Dtos.setId(to::setSubcategoryId, from.getSubcategory());
    setString(to::setThresholdType, maybeGetProperty(from.getThresholdType(), ThresholdType::name));
    setString(to::setUnits, from.getUnits());
    setInteger(to::setSortPriority, from.getSortPriority(), true);
    return to;
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

  public Metric to() {
    Metric to = new Metric();
    setLong(to::setId, getId(), false);
    setString(to::setAlias, getAlias());
    setObject(to::setCategory, getCategory(), MetricCategory::valueOf);
    setObject(to::setSubcategory, MetricSubcategory::new, getSubcategoryId());
    setObject(to::setThresholdType, getThresholdType(), ThresholdType::valueOf);
    setString(to::setUnits, getUnits());
    setInteger(to::setSortPriority, getSortPriority(), true);
    return to;
  }

}
