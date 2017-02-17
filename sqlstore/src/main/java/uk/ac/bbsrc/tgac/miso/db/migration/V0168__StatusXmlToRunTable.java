package uk.ac.bbsrc.tgac.miso.db.migration;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.xml.sax.InputSource;

/**
 * Migration to remove a xml file from the MySql database. The useful values from the xml file are copied to fields in the database.
 * 
 * Flyway migration to pull four values out of an xml file stored in the xml field of the Status table. Four new columns are added to the
 * Run table and the four values from the xml file are copied to the four new columns. The xml filed in the Status table is then deleted.
 *
 */
public class V0168__StatusXmlToRunTable implements JdbcMigration {

  @Override
  public void migrate(Connection connection) throws Exception {
    addStatusColumnsToRun(connection);
    Map<String, String> statusXml = getStatusXml(connection);
    Map<String, StatusValues> statusValues = statusXml.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> new StatusValues(entry.getValue())));
    updateStatusColumns(connection, statusValues);
    deleteXmlColumnFromStatus(connection);
  }

  private Map<String, String> getStatusXml(Connection connection) throws SQLException {
    Map<String, String> result = new HashMap<>();
    PreparedStatement getStatusXml = connection.prepareStatement("SELECT s.runName, s.xml FROM Status AS s WHERE s.xml IS NOT NULL");
    ResultSet rs = getStatusXml.executeQuery();
    while (rs.next()) {
      String key = rs.getString("runName");
      String value = rs.getString("xml");
      result.put(key, value);
    }
    return result;
  }

  private void addStatusColumnsToRun(Connection connection) throws SQLException {
    List<String> newColumnSql = new ArrayList<>();
    newColumnSql.add("ALTER TABLE Run ADD COLUMN numCycles int(11) DEFAULT NULL;");
    newColumnSql.add("ALTER TABLE Run ADD COLUMN imgCycle int(11) DEFAULT NULL;");
    newColumnSql.add("ALTER TABLE Run ADD COLUMN scoreCycle int(11) DEFAULT NULL;");
    newColumnSql.add("ALTER TABLE Run ADD COLUMN callCycle int(11) DEFAULT NULL;");
    for (String sql : newColumnSql) {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.execute();
    }
  }

  private void updateStatusColumns(Connection connection, Map<String, StatusValues> statusValues) throws SQLException {
    PreparedStatement statement = connection
        .prepareStatement("UPDATE Run SET numCycles = ?, imgCycle = ?, scoreCycle = ?, callCycle = ? WHERE alias LIKE ?;");
    statusValues.entrySet().stream().forEach(e -> {
      try {
        statement.setInt(1, e.getValue().getNumCycles());
        statement.setInt(2, e.getValue().getImgCycle());
        statement.setInt(3, e.getValue().getScoreCycle());
        statement.setInt(4, e.getValue().getCallCycle());
        statement.setString(5, e.getKey());
        statement.execute();
      } catch (SQLException error) {
        throw new RuntimeException(error.getMessage(), error);
      }
    });
  }

  private void deleteXmlColumnFromStatus(Connection connection) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("ALTER TABLE Status DROP COLUMN xml;");
    statement.execute();
  }

  class StatusValues {

    private final int numCycles;
    private final int imgCycle;
    private final int scoreCycle;
    private final int callCycle;

    public StatusValues(String xml) {
      XPath xpath = XPathFactory.newInstance().newXPath();
      String numCyclesExpression = "/Status/NumCycles/text()";
      String imgCycleExpression = "/Status/ImgCycle/text()";
      String scoreCycleExpression = "/Status/ScoreCycle/text()";
      String callCycleExpression = "/Status/CallCycle/text()";
      int numCyclesTemp = 0;
      int imgCyclesTemp = 0;
      int scoreCycleTemp = 0;
      int callCycleTemp = 0;

      try {
        numCyclesTemp = Integer.valueOf(xpath.evaluate(numCyclesExpression, new InputSource(new StringReader(xml))));
        imgCyclesTemp = Integer.valueOf(xpath.evaluate(imgCycleExpression, new InputSource(new StringReader(xml))));
        scoreCycleTemp = Integer.valueOf(xpath.evaluate(scoreCycleExpression, new InputSource(new StringReader(xml))));
        callCycleTemp = Integer.valueOf(xpath.evaluate(callCycleExpression, new InputSource(new StringReader(xml))));
      } catch (NumberFormatException | XPathExpressionException e) {
        throw new RuntimeException(e.getMessage(), e);
      } finally {
        numCycles = numCyclesTemp;
        imgCycle = imgCyclesTemp;
        scoreCycle = scoreCycleTemp;
        callCycle = callCycleTemp;
      }
    }

    public int getNumCycles() {
      return numCycles;
    }

    public int getImgCycle() {
      return imgCycle;
    }

    public int getScoreCycle() {
      return scoreCycle;
    }

    public int getCallCycle() {
      return callCycle;
    }

  }

}
