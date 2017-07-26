package uk.ac.bbsrc.tgac.miso.webapp.service.statsdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.tgac.statsdb.run.ReportTable;
import uk.ac.tgac.statsdb.run.Reports;
import uk.ac.tgac.statsdb.run.ReportsDecorator;
import uk.ac.tgac.statsdb.run.RunProperty;

public abstract class TransformReport {

  interface ReportRowConsumer {
    public void consume(ObjectNode output, Map<String, ReportTable> reportResults, int index);
  }
  private static final Logger log = LoggerFactory.getLogger(TransformReport.class);

  private String aName;

  private final List<ReportRowConsumer> consumers = new ArrayList<>();

  private final String metricType;

  public TransformReport(String metricType) {
    super();
    this.metricType = metricType;
  }

  protected void add(String reportName, String property, int columnNumber) {
    aName = reportName;
    consumers.add(
        (output, reportResults, index) -> output.put(property, reportResults.get(reportName).getTable().get(index).get(columnNumber)));
  }

  public void process(Run run, JdbcTemplate dataSource, ArrayNode output) {
    Reports reports = new Reports(dataSource);
    ReportsDecorator reportsDecorator = new ReportsDecorator(reports);
    Map<RunProperty, String> params = new EnumMap<>(RunProperty.class);
    params.put(RunProperty.run, run.getAlias());
    Map<String, ReportTable> reportResults;
    try {
      reportResults = query(reportsDecorator, params);
    } catch (SQLException e) {
      log.debug("Failed to get report", e);
      ObjectNode error = output.addObject();
      error.put("type", "message");
      error.put("message", String.format("Failed to get report for %s from STATSDB.", metricType));
      return;
    }

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode metric = mapper.createObjectNode();
    ArrayNode array = metric.putArray("data");
    int numItems = reportResults.get(aName).getTable().size();
    if (numItems < 2) {
      return;
    }
    for (int x = 0; x < numItems; x++) {
      ObjectNode data = array.addObject();
      for (ReportRowConsumer consumer : consumers) {
        consumer.consume(data, reportResults, x);
      }
    }
    metric.put("type", metricType);
    output.add(metric);
  }

  protected abstract Map<String, ReportTable> query(ReportsDecorator decorator, Map<RunProperty, String> params) throws SQLException;
}