package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.Map;
import java.util.function.Function;

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
        assertTrue(String.format(formatString, key.toString()) + " expected null, is " + fieldValue, isStringEmptyOrNull(fieldValue));
      } else {
        assertEquals(String.format(formatString, key.toString()), val, page.getField(key));
      }
    });
  }

  public static <T extends FieldElement> void assertAttribute(T field, Map<T, String> expectedValues, String actual) {
    if (!expectedValues.containsKey(field)) {
      return;
    }
    String expected = expectedValues.get(field);
    if (expected == null) {
      assertTrue(String.format("persisted attribute expected empty '%s', actual %s", field, actual), isStringEmptyOrNull(actual));
    } else {
      assertEquals(String.format("persisted attribute '%s'", field), expected, actual);
    }
  }

  public static String nullOrToString(Object maybeNull) {
    return maybeNull == null ? null : maybeNull.toString();
  }

  public static <T> String nullOrGet(T maybeNull, Function<T, String> getter) {
    return maybeNull == null ? null : getter.apply(maybeNull);
  }

  public static <T> String nullValueOrGet(T maybeNull, Function<T, String> getter, String nullValue) {
    return maybeNull == null ? nullValue : getter.apply(maybeNull);
  }

  public static String replaceIfNull(String maybeNull, String nullValue) {
    return maybeNull == null ? nullValue : maybeNull;
  }

}
