package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DefaultLibraryAliasValidatorTest {

  private DefaultLibraryAliasValidator sut;

  @Before
  public void setup() {
    sut = new DefaultLibraryAliasValidator();
  }

  @Test
  public void test() {
    assertTrue(sut.validate("RD_L1-1_Foo.bar").isValid());
  }

}
