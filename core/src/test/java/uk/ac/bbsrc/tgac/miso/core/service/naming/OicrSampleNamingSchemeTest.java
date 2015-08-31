package uk.ac.bbsrc.tgac.miso.core.service.naming;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;

public class OicrSampleNamingSchemeTest {

  private MisoNamingScheme<Sample> sut;

  @Before
  public void setUp() throws Exception {
    sut = new OicrSampleNamingScheme();
  }

  @Test
  public void test_alias() throws Exception {
    assertThat(sut.validateField("alias", "BART_1273_Br_P_nn_1-1_D"), is(true));
  }

  @Test
  public void test_alias_with_aliquot() throws Exception {
    assertThat(sut.validateField("alias", "BART_1273_Br_P_nn_1-1_D_4"), is(true));
  }

  @Test
  public void test_alias_with_stock_aliquot() throws Exception {
    assertThat(sut.validateField("alias", "BART_1273_Br_P_nn_1-1_D_S3"), is(true));
  }

}
