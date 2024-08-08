package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;

public class HandsontableUtils {

  private static final Logger log = LoggerFactory.getLogger(HandsontableUtils.class);

  private HandsontableUtils() {
    throw new IllegalStateException("Util class not intended for instantiation");
  }

  public static void testTableSetup(BulkPage page, Collection<String> expectedColumns, int expectedRows) {
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    for (String col : expectedColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals("Column count", expectedColumns.size(), headings.size());
    assertEquals("Row count", expectedRows, table.getRowCount());
  }

  public static void assertColumnValues(HandsOnTable table, int rowNum, Map<String, String> attributes,
      String hintMessage) {
    String formatString = hintMessage + " row %d column '%s' value";
    attributes.forEach((key, val) -> {
      if (isStringEmptyOrNull(val)) {
        assertTrue(String.format(formatString, rowNum, key) + " expected empty",
            isStringEmptyOrNull(table.getText(key, rowNum)));
      } else {
        assertEquals(String.format(formatString, rowNum, key), val, table.getText(key, rowNum));
      }
    });
  }

  public static void fillRow(HandsOnTable table, int rowNum, Map<String, String> attributes) {
    attributes.forEach((key, val) -> table.enterText(key, rowNum, val));
  }

  public static void saveAndAssertSuccess(HandsOnTable table) {
    saveAndAssertSuccess(table, false);
  }

  public static void saveAndAssertSuccess(HandsOnTable table, boolean confirmRequired) {
    HandsOnTableSaveResult result = table.save(confirmRequired);

    if (result.getItemsSaved() != table.getRowCount()) {
      log.error(result.printSummary());
    }

    assertEquals("Save count", table.getRowCount(), result.getItemsSaved());
    assertTrue("Server error messages", result.getServerErrors().isEmpty());
    assertTrue("Save error messages", result.getSaveErrors().isEmpty());
  }

  public static <T> void assertEntityAttribute(String column, Map<String, String> attributes, T object,
      Function<T, String> getter) {
    if (attributes.containsKey(column)) {
      String objectAttribute = getter.apply(object);
      String tableAttribute = attributes.get(column);
      if (tableAttribute == null) {
        assertTrue(String.format("persisted attribute expected empty '%s'", column),
            isStringEmptyOrNull(objectAttribute));
      } else {
        assertEquals(String.format("persisted attribute '%s'", column), tableAttribute, objectAttribute);
      }
    }
  }

  public static String emptyIfNull(BigDecimal value) {
    return value == null ? "" : StringUtils.strip(value.toPlainString(), "0");
  }

  public static String emptyIfNull(Integer value) {
    return value == null ? "" : StringUtils.stripStart(value.toString(), "0");
  }

  public static String emptyIfNull(String value) {
    return value == null ? "" : value;
  }

  public static String booleanString(Boolean value) {
    return booleanString(value, null);
  }

  public static String booleanString(Boolean value, String nullValue) {
    if (value == null) {
      return nullValue;
    } else if (Boolean.TRUE.equals(value)) {
      return "True";
    } else {
      return "False";
    }
  }

}
