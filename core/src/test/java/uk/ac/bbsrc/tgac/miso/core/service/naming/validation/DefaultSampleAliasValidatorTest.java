package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DefaultSampleAliasValidatorTest {

  private DefaultSampleAliasValidator sut;

  @Before
  public void setup() {
    sut = new DefaultSampleAliasValidator();
  }

  @Test
  public void test() {
    assertTrue(sut.validate("RD_S1_Foo.bar").isValid());
  }

}
