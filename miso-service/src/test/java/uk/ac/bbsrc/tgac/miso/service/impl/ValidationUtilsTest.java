package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValidationUtilsTest {

  private static class TestObject {
    private final Integer value;

    public TestObject(Integer value) {
      this.value = value;
    }

    public Integer getValue() {
      return value;
    }
  }

  @Test
  public void testIsSetAndChanged() {
    testIsSetAndChanged(false, new TestObject(null), null);
    testIsSetAndChanged(true, new TestObject(1), null);
    testIsSetAndChanged(false, new TestObject(null), new TestObject(null));
    testIsSetAndChanged(false, new TestObject(1), new TestObject(1));
    testIsSetAndChanged(true, new TestObject(1), new TestObject(null));
    testIsSetAndChanged(false, new TestObject(null), new TestObject(1));
    testIsSetAndChanged(true, new TestObject(1), new TestObject(2));
  }

  private void testIsSetAndChanged(boolean expected, TestObject object, TestObject beforeChange) {
    assertEquals(expected, ValidationUtils.isSetAndChanged(TestObject::getValue, object, beforeChange));
  }

  @Test
  public void testIsChanged() {
    testIsChanged(true, new TestObject(null), null);
    testIsChanged(true, new TestObject(1), null);
    testIsChanged(false, new TestObject(null), new TestObject(null));
    testIsChanged(false, new TestObject(1), new TestObject(1));
    testIsChanged(true, new TestObject(1), new TestObject(null));
    testIsChanged(true, new TestObject(null), new TestObject(1));
    testIsChanged(true, new TestObject(1), new TestObject(2));
  }

  private void testIsChanged(boolean expected, TestObject object, TestObject beforeChange) {
    assertEquals(expected, ValidationUtils.isChanged(TestObject::getValue, object, beforeChange));
  }

}
