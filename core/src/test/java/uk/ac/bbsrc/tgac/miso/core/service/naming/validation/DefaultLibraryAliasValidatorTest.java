package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultLibraryAliasValidatorTest {

  private DefaultLibraryAliasValidator sut;

  @BeforeEach
  public void setup() {
    sut = new DefaultLibraryAliasValidator();
  }

  @Test
  public void test() {
    assertTrue(sut.validate("RD_L1-1_Foo.bar").isValid());
  }

}
