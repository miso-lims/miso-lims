package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleLCMTubeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrSampleAliasGenerator;

public class OicrSampleAliasGeneratorTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private OicrSampleAliasGenerator sut;

  @Before
  public void setUp() {
    sut = new OicrSampleAliasGenerator();
  }

  @Test
  public void generateForIdentityExceptionTest() throws Exception {
    // Cannot generate alias for Identity
    Sample identity = makeIdentity();
    exception.expect(IllegalArgumentException.class);
    sut.generate(identity);
  }

  @Test
  public void generateForTissueTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1", sut.generate(makeMinimalTissue()));
    assertEquals("PROJ_0001_Bn_P_32_2-3", sut.generate(makeFullTissue()));
  }

  @Test
  public void generateForTissueProcessingTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_LCM02", sut.generate(makeLcmTube()));
  }

  @Test
  public void generateForStockFromTissueTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_D_S4", sut.generate(makeStockFromTissue()));
  }

  @Test
  public void generateForStockFromProcessingTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_D_S4", sut.generate(makeStockFromProcessing()));
  }

  @Test
  public void generateForStockFromStockTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_D_S6", sut.generate(makeStockFromStock()));
  }

  @Test
  public void generateForAliquotFromStockTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_R_12", sut.generate(makeAliquotFromStock()));
  }

  @Test
  public void generateForAliquotFromAliquotTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_R_12_MR_2", sut.generate(makeAliquotFromAliquot()));
  }

  private SampleAliquot makeAliquotFromAliquot() throws Exception {
    SampleAliquot aliquot = new SampleAliquotImpl();
    SampleAliquot parent = makeAliquotFromStock();
    parent.setAlias("PROJ_0001_nn_n_nn_1-1_R_12");
    aliquot.setParent(parent);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("MR_");
    aliquot.setSampleClass(sc);
    aliquot.setSiblingNumber(2);

    return aliquot;
  }

  private SampleAliquot makeAliquotFromStock() throws Exception {
    DetailedSample lcm = makeLcmTube();
    lcm.setAlias("PROJ_0001_nn_n_nn_1-1_LCM02");

    SampleStock stock = new SampleStockImpl();
    stock.setParent(lcm);
    SampleClass stockSc = new SampleClassImpl();
    stockSc.setSuffix("R_S");
    stock.setSampleClass(stockSc);
    stock.setSiblingNumber(4);
    stock.setAlias("PROJ_0001_nn_n_nn_1-1_R_S4");

    SampleAliquot aliquot = new SampleAliquotImpl();
    aliquot.setParent(stock);
    SampleClass aliquotSc = new SampleClassImpl();
    aliquotSc.setSuffix("R_");
    aliquot.setSampleClass(aliquotSc);
    aliquot.setSiblingNumber(12);

    return aliquot;
  }

  private Sample makeStockFromStock() throws Exception {
    SampleStock stock = new SampleStockImpl();
    SampleStock parentStock = makeStockFromTissue();
    parentStock.setAlias("PROJ_0001_nn_n_nn_1-1_D_S4");
    stock.setParent(parentStock);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("D_S");
    stock.setSampleClass(sc);
    stock.setSiblingNumber(6);
    return stock;
  }

  private Sample makeStockFromProcessing() throws Exception {
    SampleStock stock = new SampleStockImpl();
    DetailedSample lcmTube = makeLcmTube();
    lcmTube.setAlias("PROJ_0001_nn_n_nn_1-1_LCM02");
    stock.setParent(lcmTube);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("D_S");
    stock.setSampleClass(sc);
    stock.setSiblingNumber(4);
    return stock;
  }

  private SampleStock makeStockFromTissue() throws Exception {
    SampleStock stock = new SampleStockImpl();
    SampleTissue tissue = makeMinimalTissue();
    tissue.setAlias("PROJ_0001_nn_n_nn_1-1");
    stock.setParent(tissue);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("D_S");
    stock.setSampleClass(sc);
    stock.setSiblingNumber(4);
    return stock;
  }

  private SampleLCMTube makeLcmTube() {
    SampleLCMTube lcmTube = new SampleLCMTubeImpl();
    SampleTissue tissue = makeMinimalTissue();
    tissue.setAlias("PROJ_0001_nn_n_nn_1-1");
    lcmTube.setParent(tissue);
    lcmTube.setSiblingNumber(2);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("LCM");
    lcmTube.setSampleClass(sc);
    return lcmTube;
  }

  private SampleTissue makeMinimalTissue() {
    SampleTissue tissue = new SampleTissueImpl();
    tissue.setParent(makeIdentity());
    tissue.setTimesReceived(1);
    tissue.setTubeNumber(1);
    return tissue;
  }

  private Sample makeFullTissue() {
    SampleTissue tissue = makeMinimalTissue();

    TissueOrigin to = new TissueOriginImpl();
    to.setAlias("Bn");
    tissue.setTissueOrigin(to);
    TissueType tt = new TissueTypeImpl();
    tt.setAlias("P");
    tissue.setTissueType(tt);
    tissue.setPassageNumber(32);
    tissue.setTimesReceived(2);
    tissue.setTubeNumber(3);

    return tissue;
  }

  private Identity makeIdentity() {
    Identity identity = new IdentityImpl();
    identity.setId(1L);
    identity.setAlias("PROJ_0001");

    Project proj = new ProjectImpl();
    proj.setId(1L);
    proj.setAlias("PROJ");
    identity.setProject(proj);

    return identity;
  }

}
