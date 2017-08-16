package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;

public class HandsontableUtils {

  private static final Logger log = LoggerFactory.getLogger(HandsontableUtils.class);

  private HandsontableUtils() {
    throw new IllegalStateException("Util class not intended for instantiation");
  }

  public static void assertColumnValues(HandsOnTable table, int rowNum, Map<String, String> attributes, String hintMessage) {
    String formatString = hintMessage + " row %d column '%s' value";
    attributes.forEach((key, val) -> {
      if (isStringEmptyOrNull(val)) {
        assertTrue(String.format(formatString, rowNum, key) + " expected empty", isStringEmptyOrNull(table.getText(key, rowNum)));
      } else {
        assertEquals(String.format(formatString, rowNum, key), val, table.getText(key, rowNum));
      }
    });
  }

  public static void fillRow(HandsOnTable table, int rowNum, Map<String, String> attributes) {
    attributes.forEach((key, val) -> table.enterText(key, rowNum, val));
  }

  public static void saveAndAssertSuccess(HandsOnTable table) {
    HandsOnTableSaveResult result = table.save();

    if (result.getItemsSaved() != table.getRowCount()) {
      log.error(result.printSummary());
    }

    assertEquals("Save count", table.getRowCount(), result.getItemsSaved());
    assertTrue("Server error messages", result.getServerErrors().isEmpty());
    assertTrue("Save error messages", result.getSaveErrors().isEmpty());
  }

  public static <T> void assertEntityAttribute(String column, Map<String, String> attributes, T object, Function<T, String> getter) {
    if (attributes.containsKey(column)) {
      String objectAttribute = getter.apply(object);
      String tableAttribute = attributes.get(column);
      if (tableAttribute == null) {
        assertTrue(String.format("persisted attribute expected empty '%s'", column), isStringEmptyOrNull(objectAttribute));
      } else {
        assertEquals(String.format("persisted attribute '%s'", column), tableAttribute, objectAttribute);
      }
    }
  }

  public static String getQcPassedString(Boolean qcPassed) {
    if (qcPassed == null) {
      return "Unknown";
    } else if (qcPassed) {
      return "True";
    } else {
      return "False";
    }
  }

}
