package uk.ac.bbsrc.tgac.miso.webapp.service.statsdb;

import java.sql.SQLException;
import java.util.Map;

import uk.ac.tgac.statsdb.run.ReportTable;
import uk.ac.tgac.statsdb.run.ReportsDecorator;
import uk.ac.tgac.statsdb.run.RunProperty;

public class PerPositionBaseSequenceQualityReport extends TransformReport {
  public static final PerPositionBaseSequenceQualityReport INSTANCE = new PerPositionBaseSequenceQualityReport();

  public PerPositionBaseSequenceQualityReport() {
    super("statsdb-per-position-sequence-quality");
    add("quality_lower_quartile", "y", 0);
    add("quality_median", "median", 2);
    add("quality_lower_quartile", "q1", 2);
    add("quality_upper_quartile", "q3", 2);
    add("quality_10th_percentile", "low", 2);
    add("quality_90th_percentile", "high", 2);
  }

  @Override
  protected Map<String, ReportTable> query(ReportsDecorator decorator, Map<RunProperty, String> params) throws SQLException {
    return decorator.getPerPositionBaseSequenceQuality(params);
  }

}
