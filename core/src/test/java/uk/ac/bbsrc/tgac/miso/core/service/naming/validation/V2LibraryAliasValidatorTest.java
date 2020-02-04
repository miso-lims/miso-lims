package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class V2LibraryAliasValidatorTest {

  private V2LibraryAliasValidator sut;

  @Before
  public void setup() {
    sut = new V2LibraryAliasValidator();
  }

  @Test
  public void testValidAliases() {
    assertTrue(sut.validate("PROJ_0123_01_LB02").isValid());
    assertTrue(sut.validate("PRO_123_02_LB03").isValid());
    assertTrue(sut.validate("PROJECT_012345_123_LB456").isValid());
  }

  @Test
  public void testInvalidAliases() {
    assertFalse(sut.validate("PR_0123_01_LB02").isValid());
    assertFalse(sut.validate("PROJ_01_01_LB02").isValid());
    assertFalse(sut.validate("PROJ_0123_1_LB02").isValid());
    assertFalse(sut.validate("PROJ_0123_01_LB2").isValid());
    assertFalse(sut.validate("PROJ_0123_01_02").isValid());
    assertFalse(sut.validate("PROJ_0123_01").isValid());
    assertFalse(sut.validate("PROJ_0123").isValid());
    assertFalse(sut.validate("PROJ_0123_01_LB02-03").isValid());
  }

}
