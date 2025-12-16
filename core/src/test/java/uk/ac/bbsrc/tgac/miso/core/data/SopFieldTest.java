
package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.SopField.FieldType;

public class SopFieldTest {

  @Test
  public void testTextFieldValidation() {
    SopField field = new SopField();
    field.setFieldType(FieldType.TEXT);
    assertTrue(field.isValidValue("ABC123"));
    assertTrue(field.isValidValue(""));
    assertTrue(field.isValidValue(null));
  }

  @Test
  public void testNumberFieldValidation() {
    SopField field = new SopField();
    field.setFieldType(FieldType.NUMBER);
    assertTrue(field.isValidValue("42"));
    assertTrue(field.isValidValue("3.14"));
    assertFalse(field.isValidValue("ABC"));
  }

  @Test
  public void testPercentageFieldValidation() {
    SopField field = new SopField();
    field.setFieldType(FieldType.PERCENTAGE);
    assertTrue(field.isValidValue("50"));
    assertTrue(field.isValidValue("1.5"));
    assertFalse(field.isValidValue("101"));
    assertFalse(field.isValidValue("-1"));
  }
}
