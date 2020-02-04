package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class V2LibraryAliquotAliasValidatorTest {

  private V2LibraryAliquotAliasValidator sut;

  @Before
  public void setup() {
    sut = new V2LibraryAliquotAliasValidator();
  }

  @Test
  public void testValidAliases() {
    assertTrue(sut.validate("PROJ_0123_01_LB02-03").isValid());
    assertTrue(sut.validate("PRO_123_02_LB03-04").isValid());
    assertTrue(sut.validate("PROJECT_012345_123_LB456-789").isValid());
  }

  @Test
  public void testInvalidAliases() {
    assertFalse(sut.validate("PR_0123_01_LB02-03").isValid());
    assertFalse(sut.validate("PROJ_01_01_LB02-03").isValid());
    assertFalse(sut.validate("PROJ_0123_1_LB02-03").isValid());
    assertFalse(sut.validate("PROJ_0123_01_LB2-03").isValid());
    assertFalse(sut.validate("PROJ_0123_01_LB2-3").isValid());
    assertFalse(sut.validate("PROJ_0123_01_LB02").isValid());
    assertFalse(sut.validate("PROJ_0123_01").isValid());
    assertFalse(sut.validate("PROJ_0123").isValid());
  }

}
