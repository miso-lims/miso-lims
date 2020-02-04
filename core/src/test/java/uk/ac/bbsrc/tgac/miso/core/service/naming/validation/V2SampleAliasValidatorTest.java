package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class V2SampleAliasValidatorTest {

  private V2SampleAliasValidator sut;

  @Before
  public void setup() {
    sut = new V2SampleAliasValidator();
  }

  @Test
  public void testValidIdentityAlias() {
    assertTrue(sut.validate("PROJ_0123").isValid());
    assertTrue(sut.validate("PROJ_123456").isValid());
  }

  @Test
  public void testValidTissueAlias() {
    assertTrue(sut.validate("PROJ_0123_01").isValid());
    assertTrue(sut.validate("PROJ_123456_1234").isValid());
  }

  @Test
  public void testValidLevel3Alias() {
    assertTrue(sut.validate("PROJ_0123_01_SG01").isValid());
    assertTrue(sut.validate("PROJ_123456_1234_LB1234").isValid());
  }

  @Test
  public void testValidLevel4Alias() {
    assertTrue(sut.validate("PROJ_0123_01_SG01-01").isValid());
    assertTrue(sut.validate("PROJ_123456_1234_LB1234-1234").isValid());
  }

  @Test
  public void testBlankInvalid() {
    assertFalse(sut.validate("").isValid());
    assertFalse(sut.validate(" ").isValid());
    assertFalse(sut.validate(null).isValid());
  }

  @Test
  public void testNonPaddedInvalid() {
    assertFalse(sut.validate("PROJ_1").isValid());
    assertFalse(sut.validate("PROJ_0001_1").isValid());
    assertFalse(sut.validate("PROJ_0001_01_SG1").isValid());
    assertFalse(sut.validate("PROJ_0001_01_SG01-1").isValid());
  }

}
