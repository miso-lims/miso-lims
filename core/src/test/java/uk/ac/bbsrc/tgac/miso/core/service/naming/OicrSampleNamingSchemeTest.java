package uk.ac.bbsrc.tgac.miso.core.service.naming;

import static org.junit.Assert.assertTrue;

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
  public void testValidIdentityAlias() throws Exception {
    assertTrue(sut.validateField("alias", "PROJ_1234"));
    assertTrue(sut.validateField("alias", "PROJ2_1234"));
  }
  
  @Test
  public void testValidTissueAlias() throws Exception {
    assertTrue(sut.validateField("alias", "PROJ_1234_nn_n_nn_1-1"));
    assertTrue(sut.validateField("alias", "PROJ_1234_Br_P_32_1-1"));
    assertTrue(sut.validateField("alias", "PROJ2_1234_Br_P_32_1-1"));
  }
  
  @Test
  public void testValidTissueProcessingAlias() throws Exception {
    assertTrue(sut.validateField("alias", "PROJ_1234_nn_n_nn_1-1_CV01"));
    assertTrue(sut.validateField("alias", "PROJ2_1234_nn_n_nn_1-1_CV01"));
  }
  
  @Test
  public void testValidAnalyteAlias() throws Exception {
    assertTrue(sut.validateField("alias", "PROJ_1234_nn_n_nn_1-1_D_S1"));
    assertTrue(sut.validateField("alias", "PROJ_1234_nn_n_nn_1-1_R_S1"));
    assertTrue(sut.validateField("alias", "PROJ_1234_nn_n_nn_1-1_D_1"));
    assertTrue(sut.validateField("alias", "PROJ_1234_nn_n_nn_1-1_R_1"));
    assertTrue(sut.validateField("alias", "PROJ2_1234_nn_n_nn_1-1_R_1"));
    assertTrue(sut.validateField("alias", "PROJ_1234_nn_n_nn_1-1_R_1_SM_1"));
  }

}
