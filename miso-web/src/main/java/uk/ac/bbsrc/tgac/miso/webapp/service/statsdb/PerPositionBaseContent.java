package uk.ac.bbsrc.tgac.miso.webapp.service.statsdb;

import java.sql.SQLException;
import java.util.Map;

import uk.ac.tgac.statsdb.run.ReportTable;
import uk.ac.tgac.statsdb.run.ReportsDecorator;
import uk.ac.tgac.statsdb.run.RunProperty;

public class PerPositionBaseContent extends TransformReport {
  public static final PerPositionBaseContent INSTANCE = new PerPositionBaseContent();
  public PerPositionBaseContent() {
    super("statsdb-per-position-base-content");
    add("base_content_a", "base", 0);
    add("base_content_a", "A", 2);
    add("base_content_c", "C", 2);
    add("base_content_g", "G", 2);
    add("base_content_t", "T", 2);
  }

  @Override
  protected Map<String, ReportTable> query(ReportsDecorator decorator, Map<RunProperty, String> params) throws SQLException {
    return decorator.getPerPositionBaseContent(params);
  }

}
