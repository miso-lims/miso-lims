package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrLibraryAliasValidator;

public class OicrLibraryAliasValidatorTest {

  private OicrLibraryAliasValidator sut;

  @Before
  public void setUp() throws Exception {
    sut = new OicrLibraryAliasValidator();
  }

  @Test
  public void test_alias_01() throws Exception {
    assertThat(sut.validate("BART_1273_Br_P_PE_300_WG").isValid(), is(true));
  }

  @Test
  public void test_alias_02() throws Exception {
    assertThat(sut.validate("AOE_1273_Br_P_PE_1K_WG").isValid(), is(true));
  }

  @Test
  public void test_alias_03() throws Exception {
    assertThat(sut.validate("MOUSE_123_Br_R_SE_1K_WG").isValid(), is(true));
  }

  @Test
  public void test_alias_04() throws Exception {
    assertThat(sut.validate("MOUSE_1C3_Br_R_SE_1K_WG").isValid(), is(true));
  }

  @Test
  public void test_alias_05() throws Exception {
    assertThat(sut.validate("MOUSE_1R45_Br_R_SE_1K_WG").isValid(), is(true));
  }

}
