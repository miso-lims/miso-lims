package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SopFieldImpl;

public class SopFieldTest {

  @Test
  public void testNumberFieldValidation() {
    SopField field = new SopFieldImpl();
    field.setFieldTypeEnum(SopField.FieldType.NUMBER);

    assertTrue(field.isValidValue("42"));
    assertTrue(field.isValidValue("3.14"));
    assertFalse(field.isValidValue("ABC"));

    assertTrue(field.isValidValue(null));
    assertTrue(field.isValidValue(""));
  }

  @Test
  public void testPercentageFieldValidation() {
    SopField field = new SopFieldImpl();
    field.setFieldTypeEnum(SopField.FieldType.PERCENTAGE);

    assertTrue(field.isValidValue("50"));
    assertTrue(field.isValidValue("1.5"));
    assertFalse(field.isValidValue("101"));
    assertFalse(field.isValidValue("-1"));


    assertTrue(field.isValidValue(null));
    assertTrue(field.isValidValue(""));
  }
}
