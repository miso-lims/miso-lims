package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;

public class AssayMetricDto {

  private long id; // this is the metricId
  private String minimumThreshold;
  private String maximumThreshold;

  public static AssayMetricDto from(AssayMetric from) {
    AssayMetricDto to = new AssayMetricDto();
    setLong(to::setId, maybeGetProperty(from.getMetric(), Metric::getId), false);
    setString(to::setMinimumThreshold, from.getMinimumThreshold());
    setString(to::setMaximumThreshold, from.getMaximumThreshold());
    return to;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getMinimumThreshold() {
    return minimumThreshold;
  }

  public void setMinimumThreshold(String minimumThreshold) {
    this.minimumThreshold = minimumThreshold;
  }

  public String getMaximumThreshold() {
    return maximumThreshold;
  }

  public void setMaximumThreshold(String maximumThreshold) {
    this.maximumThreshold = maximumThreshold;
  }

  public AssayMetric to() {
    AssayMetric to = new AssayMetric();
    setObject(to::setMetric, Metric::new, getId());
    setBigDecimal(to::setMinimumThreshold, getMinimumThreshold());
    setBigDecimal(to::setMaximumThreshold, getMaximumThreshold());
    return to;
  }

}
