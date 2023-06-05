package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissuePieceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;
import uk.ac.bbsrc.tgac.miso.core.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;

public class V2SampleAliasGeneratorTest {

  @Mock
  private SampleNumberPerProjectService sampleNumberPerProjectService;

  @Mock
  private SiblingNumberGenerator siblingNumberGenerator;

  @InjectMocks
  private V2SampleAliasGenerator sut;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void generateForIdentityTest() throws Exception {
    Sample identity = makeIdentity();
    Mockito.when(sampleNumberPerProjectService.nextNumber(Mockito.any(), Mockito.anyString())).thenReturn("0123");
    assertEquals("PROJ_0123", sut.generate(identity));
  }

  @Test
  public void generateForTissueTest() throws Exception {
    mockFirstAvailableSiblingNumber(1);
    assertEquals("PROJ_0001_01", sut.generate(makeTissue()));
  }

  @Test
  public void generateForTissueFromTissueTest() throws Exception {
    mockFirstAvailableSiblingNumber(2);
    assertEquals("PROJ_0001_02", sut.generate(makeTissueFromTissue()));
  }

  @Test
  public void generateForTissueProcessingTest() throws Exception {
    mockFirstAvailableSiblingNumber(3);
    assertEquals("PROJ_0001_01_TL03", sut.generate(makeLcmTube()));
  }

  @Test
  public void generateForStockFromTissueTest() throws Exception {
    mockFirstAvailableSiblingNumber(4);
    assertEquals("PROJ_0001_01_SG04", sut.generate(makeStockFromTissue()));
  }

  @Test
  public void generateForStockFromProcessingTest() throws Exception {
    mockFirstAvailableSiblingNumber(5);
    assertEquals("PROJ_0001_01_SG05", sut.generate(makeStockFromProcessing()));
  }

  @Test
  public void generateForStockFromStockTest() throws Exception {
    mockFirstAvailableSiblingNumber(6);
    assertEquals("PROJ_0001_01_SG06", sut.generate(makeStockFromStock()));
  }

  @Test
  public void generateForAliquotFromStockTest() throws Exception {
    mockFirstAvailableSiblingNumber(7);
    assertEquals("PROJ_0001_01_SG04-07", sut.generate(makeAliquotFromStock()));
  }

  @Test
  public void generateForAliquotFromAliquotTest() throws Exception {
    mockFirstAvailableSiblingNumber(8);
    assertEquals("PROJ_0001_01_SG04-08", sut.generate(makeAliquotFromAliquot()));
  }

  private SampleAliquot makeAliquotFromAliquot() throws Exception {
    SampleAliquot aliquot = new SampleAliquotImpl();
    SampleAliquot parent = makeAliquotFromStock();
    parent.setAlias("PROJ_1234567_SG04-05");
    SampleClass parentSc = new SampleClassImpl();
    parentSc.setSampleCategory(SampleAliquot.CATEGORY_NAME);
    aliquot.setParent(parent);
    SampleClass sc = new SampleClassImpl();
    sc.setSampleCategory(SampleAliquot.CATEGORY_NAME);
    aliquot.setSampleClass(sc);
    aliquot.setSiblingNumber(2);

    return aliquot;
  }

  private SampleAliquot makeAliquotFromStock() throws Exception {
    DetailedSample lcm = makeLcmTube();
    lcm.setAlias("PROJ_0001_01_TL_03");

    SampleStock stock = new SampleStockImpl();
    stock.setParent(lcm);
    SampleClass stockSc = new SampleClassImpl();
    stockSc.setSampleCategory(SampleStock.CATEGORY_NAME);
    stockSc.setV2NamingCode("SG");
    stock.setSampleClass(stockSc);
    stock.setSiblingNumber(4);
    stock.setAlias("PROJ_0001_01_SG04");

    SampleAliquot aliquot = new SampleAliquotImpl();
    aliquot.setParent(stock);
    SampleClass aliquotSc = new SampleClassImpl();
    aliquotSc.setSampleCategory(SampleAliquot.CATEGORY_NAME);
    aliquot.setSampleClass(aliquotSc);
    aliquot.setSiblingNumber(12);

    return aliquot;
  }

  private Sample makeStockFromStock() throws Exception {
    SampleStock stock = new SampleStockImpl();
    SampleStock parentStock = makeStockFromTissue();
    parentStock.setAlias("PROJ_0001_01_SG04");
    stock.setParent(parentStock);
    SampleClass sc = new SampleClassImpl();
    sc.setSampleCategory(SampleStock.CATEGORY_NAME);
    sc.setV2NamingCode("SG");
    stock.setSampleClass(sc);
    stock.setSiblingNumber(6);
    return stock;
  }

  private Sample makeStockFromProcessing() throws Exception {
    SampleStock stock = new SampleStockImpl();
    DetailedSample lcmTube = makeLcmTube();
    lcmTube.setAlias("PROJ_0001_TL03");
    stock.setParent(lcmTube);
    SampleClass sc = new SampleClassImpl();
    sc.setSampleCategory(SampleStock.CATEGORY_NAME);
    sc.setV2NamingCode("SG");
    stock.setSampleClass(sc);
    stock.setSiblingNumber(4);
    return stock;
  }

  private SampleStock makeStockFromTissue() throws Exception {
    SampleStock stock = new SampleStockImpl();
    SampleTissue tissue = makeTissue();
    tissue.setAlias("PROJ_0001_01");
    stock.setParent(tissue);
    SampleClass sc = new SampleClassImpl();
    sc.setSampleCategory(SampleStock.CATEGORY_NAME);
    sc.setV2NamingCode("SG");
    stock.setSampleClass(sc);
    stock.setSiblingNumber(4);
    return stock;
  }

  private SampleTissue makeTissueFromTissue() throws Exception {
    SampleTissue parent = makeTissue();
    parent.setAlias("PROJ_0001_01");
    parent.setParent(makeIdentity());
    SampleTissue child = new SampleTissueImpl();
    SampleClass sc = new SampleClassImpl();
    sc.setSampleCategory(SampleTissue.CATEGORY_NAME);
    child.setSampleClass(sc);
    child.setParent(parent);
    return child;
  }

  private SampleTissuePiece makeLcmTube() {
    SampleTissuePiece lcmTube = new SampleTissuePieceImpl();
    SampleTissue tissue = makeTissue();
    tissue.setAlias("PROJ_0001_01");
    lcmTube.setParent(tissue);
    lcmTube.setSiblingNumber(2);
    SampleClass sc = new SampleClassImpl();
    sc.setSampleCategory(SampleTissueProcessing.CATEGORY_NAME);
    lcmTube.setSampleClass(sc);
    TissuePieceType pieceType = new TissuePieceType();
    pieceType.setV2NamingCode("TL");
    lcmTube.setTissuePieceType(pieceType);
    return lcmTube;
  }

  private SampleTissue makeTissue() {
    SampleTissue tissue = new SampleTissueImpl();
    SampleClass sc = new SampleClassImpl();
    sc.setSampleCategory(SampleTissue.CATEGORY_NAME);
    tissue.setSampleClass(sc);
    tissue.setParent(makeIdentity());
    tissue.setTimesReceived(1);
    tissue.setTubeNumber(1);
    return tissue;
  }

  private SampleIdentity makeIdentity() {
    SampleIdentity identity = new SampleIdentityImpl();
    identity.setId(1L);
    identity.setAlias("PROJ_0001");
    SampleClass sc = new SampleClassImpl();
    sc.setSampleCategory(SampleIdentity.CATEGORY_NAME);
    identity.setSampleClass(sc);

    Project proj = new ProjectImpl();
    proj.setId(1L);
    proj.setTitle("PROJ");
    proj.setCode("PROJ");
    identity.setProject(proj);

    return identity;
  }

  private void mockFirstAvailableSiblingNumber(int siblingNumber) throws IOException {
    Mockito.when(siblingNumberGenerator.getFirstAvailableSiblingNumber(Mockito.any(), Mockito.anyString()))
        .thenReturn(siblingNumber);
  }

}
