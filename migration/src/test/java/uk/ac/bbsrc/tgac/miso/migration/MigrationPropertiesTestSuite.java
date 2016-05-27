package uk.ac.bbsrc.tgac.miso.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MigrationPropertiesTestSuite {

  private static final String testProperties = "target/test-classes/MigrationPropertiesTest.properties";
  
  private static final String VALID_STRING = "validString";
  private static final String VALID_INT = "validInt";
  private static final String VALID_LONG = "validLong";
  
  private static final String VALID_STRING_VALUE = "ok";
  private static final int VALID_INT_VALUE = 123;
  private static final long VALID_LONG_VALUE = 9999999999L;
  
  private static final String NEEDS_TRIMMED_STRING = "needsTrimmedString";
  private static final String NEEDS_TRIMMED_INT = "needsTrimmedInt";
  private static final String NEEDS_TRIMMED_LONG = "needsTrimmedLong";
  
  private static final String MISSING_PROPERTY = "missingProperty";
  private static final String EMPTY_PROPERTY = "emptyProperty";
  private static final String SPACES_PROPERTY = "spacesProperty";
  
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  private MigrationProperties sut;
  
  @Before
  public void setUp() throws FileNotFoundException, IOException {
    sut = new MigrationProperties(testProperties);
  }
  
  @Test
  public void testGetStringOrNullValid() {
    assertEquals(VALID_STRING_VALUE, sut.getStringOrNull(VALID_STRING));
  }
  
  @Test
  public void testGetStringOrNullNeedsTrimmed() {
    assertEquals(VALID_STRING_VALUE, sut.getStringOrNull(NEEDS_TRIMMED_STRING));
  }
  
  @Test
  public void testGetStringOrNullMissing() {
    assertNull(sut.getStringOrNull(MISSING_PROPERTY));
  }
  
  @Test
  public void testGetStringOrNullEmpty() {
    assertNull(sut.getStringOrNull(EMPTY_PROPERTY));
  }
  
  @Test
  public void testGetStringOrNullSpaces() {
    assertNull(sut.getStringOrNull(SPACES_PROPERTY));
  }
  
  @Test
  public void testGetRequiredStringValid() {
    assertEquals(VALID_STRING_VALUE, sut.getRequiredString(VALID_STRING));
  }
  
  @Test
  public void testGetRequiredStringNeedsTrimmed() {
    assertEquals(VALID_STRING_VALUE, sut.getRequiredString(NEEDS_TRIMMED_STRING));
  }
  
  @Test
  public void testGetRequiredStringMissing() {
    exception.expect(IllegalArgumentException.class);
    sut.getRequiredString(MISSING_PROPERTY);
  }
  
  @Test
  public void testGetRequiredStringEmpty() {
    exception.expect(IllegalArgumentException.class);
    sut.getRequiredString(EMPTY_PROPERTY);
  }
  
  @Test
  public void testGetRequiredStringSpaces() {
    exception.expect(IllegalArgumentException.class);
    sut.getRequiredString(SPACES_PROPERTY);
  }
  
  @Test
  public void testGetRequiredIntValid() {
    assertEquals(VALID_INT_VALUE, sut.getRequiredInt(VALID_INT));
  }
  
  @Test
  public void testGetRequiredIntNeedsTrimmed() {
    assertEquals(VALID_INT_VALUE, sut.getRequiredInt(NEEDS_TRIMMED_INT));
  }
  
  @Test
  public void testGetRequiredIntMissing() {
    exception.expect(IllegalArgumentException.class);
    sut.getRequiredInt(MISSING_PROPERTY);
  }
  
  @Test
  public void testGetRequiredIntEmpty() {
    exception.expect(IllegalArgumentException.class);
    sut.getRequiredInt(EMPTY_PROPERTY);
  }
  
  @Test
  public void testGetRequiredIntSpaces() {
    exception.expect(IllegalArgumentException.class);
    sut.getRequiredInt(SPACES_PROPERTY);
  }
  
  @Test
  public void testGetRequiredLongValid() {
    assertEquals(VALID_LONG_VALUE, sut.getRequiredLong(VALID_LONG));
  }
  
  @Test
  public void testGetRequiredLongNeedsTrimmed() {
    assertEquals(VALID_LONG_VALUE, sut.getRequiredLong(NEEDS_TRIMMED_LONG));
  }
  
  @Test
  public void testGetRequiredLongMissing() {
    exception.expect(IllegalArgumentException.class);
    sut.getRequiredLong(MISSING_PROPERTY);
  }
  
  @Test
  public void testGetRequiredLongEmpty() {
    exception.expect(IllegalArgumentException.class);
    sut.getRequiredLong(EMPTY_PROPERTY);
  }
  
  @Test
  public void testGetRequiredLongSpaces() {
    exception.expect(IllegalArgumentException.class);
    sut.getRequiredLong(SPACES_PROPERTY);
  }

}
