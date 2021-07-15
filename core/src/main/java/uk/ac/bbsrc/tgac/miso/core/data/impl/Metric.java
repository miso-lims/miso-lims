package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.ThresholdType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Metric implements Aliasable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;
  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long metricId;

  private String alias;

  @Enumerated(EnumType.STRING)
  private MetricCategory category;

  @Enumerated(EnumType.STRING)
  private ThresholdType thresholdType;

  private String units;

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

  @Override
  public int hashCode() {
    return Objects.hash(metricId, alias, category, thresholdType, units);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        Metric::getId,
        Metric::getAlias,
        Metric::getCategory,
        Metric::getThresholdType,
        Metric::getUnits);
  }

}
