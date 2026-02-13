package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class SopFieldTest {

  @Test
  public void testNumberFieldValidation() {
    SopField field = new SopField();
    field.setFieldType(SopField.FieldType.NUMBER);

    assertTrue(field.isValidValue("42"));
    assertTrue(field.isValidValue("3.14"));
    assertFalse(field.isValidValue("ABC"));
    assertTrue(field.isValidValue("50"));
    assertTrue(field.isValidValue("1.5"));
    assertTrue(field.isValidValue("101"));
    assertTrue(field.isValidValue("-1"));

    assertTrue(field.isValidValue(null));
    assertTrue(field.isValidValue(""));
  }

}
