package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class OicrSampleAliasValidatorTest {

  private OicrSampleAliasValidator sut;

  @Before
  public void setUp() throws Exception {
    sut = new OicrSampleAliasValidator();
  }

  @Test
  public void testValidIdentityAlias() throws Exception {
    assertTrue(sut.validate("PROJ_1234").isValid());
  }
  
  @Test
  public void testValidTissueAlias() throws Exception {
    assertTrue(sut.validate("PROJ_1234_nn_n_nn_1-1").isValid());
    assertTrue(sut.validate("PROJ_1234567_Br_P_32_1-1").isValid());
  }
  
  @Test
  public void testValidTissueProcessingAlias() throws Exception {
    assertTrue(sut.validate("PROJ_1234_nn_n_nn_1-1_CV01").isValid());
  }
  
  @Test
  public void testValidAnalyteAlias() throws Exception {
    assertTrue(sut.validate("PROJ_1234_nn_n_nn_1-1_D_S1").isValid());
    assertTrue(sut.validate("PROJ_1234_nn_n_nn_1-1_R_S1").isValid());
    assertTrue(sut.validate("PROJ_1234_nn_n_nn_1-1_D_1").isValid());
    assertTrue(sut.validate("PROJ_1234_nn_n_nn_1-1_R_1").isValid());
    assertTrue(sut.validate("PROJ_1234_nn_n_nn_1-1_R_1_SM_1").isValid());
  }

}
