package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultSampleAliasValidatorTest {

  private DefaultSampleAliasValidator sut;

  @BeforeEach
  public void setup() {
    sut = new DefaultSampleAliasValidator();
  }

  @Test
  public void test() {
    assertTrue(sut.validate("RD_S1_Foo.bar").isValid());
  }

}
