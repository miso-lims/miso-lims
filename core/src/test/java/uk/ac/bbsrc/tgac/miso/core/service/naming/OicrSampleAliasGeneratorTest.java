package uk.ac.bbsrc.tgac.miso.core.service.naming;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAnalyteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;

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
    sut.generateName(identity);
  }
  
  @Test
  public void generateForTissueTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1", sut.generateName(makeMinimalTissue()));
    assertEquals("PROJ_0001_Bn_P_32_2-3", sut.generateName(makeFullTissue()));
  }
  
  @Test
  public void generateForTissueProcessingTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_LCM02", sut.generateName(makeLcmTube()));
  }
  
  @Test
  public void generateForStockFromTissueTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_D_S4", sut.generateName(makeStockFromTissue()));
  }
  
  @Test
  public void generateForStockFromProcessingTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_D_S4", sut.generateName(makeStockFromProcessing()));
  }
  
  @Test
  public void generateForStockFromStockTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_D_S6", sut.generateName(makeStockFromStock()));
  }
  
  @Test
  public void generateForStockFromIdentity() throws Exception {
    assertEquals("PROJ_0001_Bn_P_nn_5-6_D_S7", sut.generateName(makeStockFromIdentity()));
  }
  
  @Test
  public void generateForAliquotFromStockTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_R_12", sut.generateName(makeAliquotFromStock()));
  }
  
  @Test
  public void generateForAliquotFromAliquotTest() throws Exception {
    assertEquals("PROJ_0001_nn_n_nn_1-1_R_12_MR_2", sut.generateName(makeAliquotFromAliquot()));
  }
  
  private Sample makeAliquotFromAliquot() throws Exception {
    Sample aliquot = new SampleImpl();
    
    SampleAdditionalInfo sai = new SampleAdditionalInfoImpl();
    Sample parent = makeAliquotFromStock();
    parent.setAlias("PROJ_0001_nn_n_nn_1-1_R_12");
    sai.setParent(parent);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("MR_");
    sai.setSampleClass(sc);
    sai.setSiblingNumber(2);
    aliquot.setSampleAdditionalInfo(sai);
    
    SampleAnalyte sa = new SampleAnalyteImpl();
    aliquot.setSampleAnalyte(sa);
    
    return aliquot;
  }
  
  private Sample makeAliquotFromStock() throws Exception {
    Sample lcm = makeLcmTube();
    lcm.setAlias("PROJ_0001_nn_n_nn_1-1_LCM02");
    
    Sample stock = new SampleImpl();
    SampleAdditionalInfo stockSai = new SampleAdditionalInfoImpl();
    stockSai.setParent(lcm);
    SampleClass stockSc = new SampleClassImpl();
    stockSc.setSuffix("R_S");
    stockSc.setStock(true);
    stockSai.setSampleClass(stockSc);
    stockSai.setSiblingNumber(4);
    stock.setSampleAdditionalInfo(stockSai);
    SampleAnalyte stockSa = new SampleAnalyteImpl();
    stock.setSampleAnalyte(stockSa);
    stock.setAlias("PROJ_0001_nn_n_nn_1-1_R_S4");
    
    
    Sample aliquot = new SampleImpl();
    SampleAdditionalInfo aliquotSai = new SampleAdditionalInfoImpl();
    aliquotSai.setParent(stock);
    SampleClass aliquotSc = new SampleClassImpl();
    aliquotSc.setSuffix("R_");
    aliquotSai.setSampleClass(aliquotSc);
    aliquotSai.setSiblingNumber(12);
    aliquot.setSampleAdditionalInfo(aliquotSai);
    SampleAnalyte aliquotSa = new SampleAnalyteImpl();
    aliquot.setSampleAnalyte(aliquotSa);
    
    return aliquot;
  }
  
  private Sample makeStockFromIdentity() throws Exception {
    Sample stock = new SampleImpl();
    
    SampleAdditionalInfo sai = new SampleAdditionalInfoImpl();
    Sample parent = makeIdentity();
    sai.setParent(parent);
    sai.setTimesReceived(5);
    sai.setTubeNumber(6);
    TissueOrigin to = new TissueOriginImpl();
    to.setAlias("Bn");
    sai.setTissueOrigin(to);
    TissueType tt = new TissueTypeImpl();
    tt.setAlias("P");
    sai.setTissueType(tt);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("D_S");
    sc.setStock(true);
    sai.setSampleClass(sc);
    sai.setSiblingNumber(7);
    stock.setSampleAdditionalInfo(sai);
    
    SampleAnalyte sa = new SampleAnalyteImpl();
    stock.setSampleAnalyte(sa);
    
    return stock;
  }
  
  private Sample makeStockFromStock() throws Exception {
    Sample stock = new SampleImpl();
    
    SampleAdditionalInfo sai = new SampleAdditionalInfoImpl();
    Sample parentStock = makeStockFromTissue();
    parentStock.setAlias("PROJ_0001_nn_n_nn_1-1_D_S4");
    sai.setParent(parentStock);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("D_S");
    sc.setStock(true);
    sai.setSampleClass(sc);
    sai.setSiblingNumber(6);
    stock.setSampleAdditionalInfo(sai);
    
    SampleAnalyte sa = new SampleAnalyteImpl();
    stock.setSampleAnalyte(sa);
    
    return stock;
  }
  
  private Sample makeStockFromProcessing() throws Exception {
    Sample stock = new SampleImpl();
    
    SampleAdditionalInfo sai = new SampleAdditionalInfoImpl();
    Sample lcmTube = makeLcmTube();
    lcmTube.setAlias("PROJ_0001_nn_n_nn_1-1_LCM02");
    sai.setParent(lcmTube);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("D_S");
    sc.setStock(true);
    sai.setSampleClass(sc);
    stock.setSampleAdditionalInfo(sai);
    sai.setSiblingNumber(4);
    
    SampleAnalyte sa = new SampleAnalyteImpl();
    stock.setSampleAnalyte(sa);
    
    return stock;
  }
  
  private Sample makeStockFromTissue() throws Exception {
    Sample stock = new SampleImpl();
    
    SampleAdditionalInfo sai = new SampleAdditionalInfoImpl();
    Sample tissue = makeMinimalTissue();
    tissue.setAlias("PROJ_0001_nn_n_nn_1-1");
    sai.setParent(tissue);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("D_S");
    sc.setStock(true);
    sai.setSampleClass(sc);
    stock.setSampleAdditionalInfo(sai);
    sai.setSiblingNumber(4);
    
    SampleAnalyte sa = new SampleAnalyteImpl();
    stock.setSampleAnalyte(sa);
    
    return stock;
  }
  
  private Sample makeLcmTube() {
    Sample lcmTube = new SampleImpl();
    
    SampleAdditionalInfo sai = new SampleAdditionalInfoImpl();
    Sample tissue = makeMinimalTissue();
    tissue.setAlias("PROJ_0001_nn_n_nn_1-1");
    sai.setParent(tissue);
    sai.setSiblingNumber(2);
    SampleClass sc = new SampleClassImpl();
    sc.setSuffix("LCM");
    sai.setSampleClass(sc);
    lcmTube.setSampleAdditionalInfo(sai);
    
    return lcmTube;
  }
  
  private Sample makeMinimalTissue() {
    Sample tissue = new SampleImpl();
    
    SampleAdditionalInfo sai = new SampleAdditionalInfoImpl();
    sai.setParent(makeIdentity());
    sai.setTimesReceived(1);
    sai.setTubeNumber(1);
    tissue.setSampleAdditionalInfo(sai);
    
    SampleTissue st = new SampleTissueImpl();
    tissue.setSampleTissue(st);
    
    return tissue;
  }
  
  private Sample makeFullTissue() {
    Sample tissue = makeMinimalTissue();
    
    TissueOrigin to = new TissueOriginImpl();
    to.setAlias("Bn");
    tissue.getSampleAdditionalInfo().setTissueOrigin(to);
    TissueType tt = new TissueTypeImpl();
    tt.setAlias("P");
    tissue.getSampleAdditionalInfo().setTissueType(tt);
    tissue.getSampleAdditionalInfo().setPassageNumber(32);
    tissue.getSampleAdditionalInfo().setTimesReceived(2);
    tissue.getSampleAdditionalInfo().setTubeNumber(3);
    
    return tissue;
  }
  
  private Sample makeIdentity() {
    Sample identity = new SampleImpl();
    identity.setId(1L);
    identity.setAlias("PROJ_0001");
    
    Project proj = new ProjectImpl();
    proj.setId(1L);
    proj.setAlias("PROJ");
    identity.setProject(proj);
    
    SampleAdditionalInfo sai = new SampleAdditionalInfoImpl();
    identity.setSampleAdditionalInfo(sai);
    
    
    Identity si = new IdentityImpl();
    identity.setIdentity(si);
    
    return identity;
  }

}
