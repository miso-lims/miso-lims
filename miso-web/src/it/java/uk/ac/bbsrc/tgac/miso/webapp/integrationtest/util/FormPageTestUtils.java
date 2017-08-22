package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.Map;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.FormPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.FormPage.FieldElement;

public class FormPageTestUtils {

  private FormPageTestUtils() {
    throw new IllegalStateException("Util class not intended for instantiation");
  }

  public static <T extends FieldElement> void assertFieldValues(String hintMessage, Map<T, String> fields, FormPage<T> page) {
    String formatString = hintMessage + " field '%s' value";
    fields.forEach((key, val) -> {
      String fieldValue = page.getField(key);
      if (val == null) {
        assertTrue(String.format(formatString, key.toString()) + " expected null", isStringEmptyOrNull(fieldValue));
      } else {
        assertEquals(String.format(formatString, key.toString()), val, page.getField(key));
      }
    });
  }

  public static void assertAttribute(Object field, String expected, String actual) {
    if (expected == null) {
      assertTrue(String.format("persisted attribute expected empty '%s'", field), isStringEmptyOrNull(actual));
    } else {
      assertEquals(String.format("persisted attribute '%s'", field), expected, actual);
    }
  }

}
