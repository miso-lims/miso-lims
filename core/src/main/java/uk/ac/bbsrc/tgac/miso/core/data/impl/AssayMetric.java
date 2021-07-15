package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Assay_Metric")
@IdClass(AssayMetric.AssayMetricId.class)
public class AssayMetric implements Serializable {

  private static final long serialVersionUID = 1L;

  public static class AssayMetricId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Assay assay;

    private Metric metric;

    public Assay getAssay() {
      return assay;
    }

    public void setAssay(Assay assay) {
      this.assay = assay;
    }

    public Metric getMetric() {
      return metric;
    }

    public void setMetric(Metric metric) {
      this.metric = metric;
    }

    @Override
    public int hashCode() {
      return Objects.hash(assay, metric);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          AssayMetricId::getAssay,
          AssayMetricId::getMetric);
    }

  }

  @Id
  @ManyToOne
  @JoinColumn(name = "assayId")
  private Assay assay;

  @Id
  @ManyToOne
  @JoinColumn(name = "metricId")
  private Metric metric;

  private BigDecimal minimumThreshold;
  private BigDecimal maximumThreshold;

  public Assay getAssay() {
    return assay;
  }

  public void setAssay(Assay assay) {
    this.assay = assay;
  }

  public Metric getMetric() {
    return metric;
  }

  public void setMetric(Metric metric) {
    this.metric = metric;
  }

  public BigDecimal getMinimumThreshold() {
    return minimumThreshold;
  }

  public void setMinimumThreshold(BigDecimal minimumThreshold) {
    this.minimumThreshold = minimumThreshold;
  }

  public BigDecimal getMaximumThreshold() {
    return maximumThreshold;
  }

  public void setMaximumThreshold(BigDecimal maximumThreshold) {
    this.maximumThreshold = maximumThreshold;
  }

  @Override
  public int hashCode() {
    return Objects.hash(assay, metric, minimumThreshold, maximumThreshold);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        AssayMetric::getAssay,
        AssayMetric::getMetric,
        AssayMetric::getMinimumThreshold,
        AssayMetric::getMaximumThreshold);
  }

}
