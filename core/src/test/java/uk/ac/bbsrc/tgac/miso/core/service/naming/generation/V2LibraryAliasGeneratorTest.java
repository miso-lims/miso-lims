package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;

public class V2LibraryAliasGeneratorTest {

  @Mock
  private SiblingNumberGenerator siblingNumberGenerator;

  @InjectMocks
  private V2LibraryAliasGenerator sut;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGenerate() throws Exception {
    Library lib = constructHierarchy("0123", "04");
    mockFirstAvailableSiblingNumber(7);
    assertEquals("TEST_0123_04_LB07", sut.generate(lib));
  }

  @Test
  public void testGenerateForShortIdentityNumber() throws Exception {
    Library lib = constructHierarchy("123", "05");
    mockFirstAvailableSiblingNumber(8);
    assertEquals("TEST_123_05_LB08", sut.generate(lib));
  }

  @Test
  public void testGenerateForLongIdentityNumber() throws Exception {
    Library lib = constructHierarchy("012345", "06");
    mockFirstAvailableSiblingNumber(12);
    assertEquals("TEST_012345_06_LB12", sut.generate(lib));
  }

  @Test
  public void testGenerateForLongLibraryNumber() throws Exception {
    Library lib = constructHierarchy("0123", "07");
    mockFirstAvailableSiblingNumber(1234);
    assertEquals("TEST_0123_07_LB1234", sut.generate(lib));
  }

  private static Library constructHierarchy(String identityNumber, String tissueNumber) {
    SampleIdentity identity = new SampleIdentityImpl();
    identity.setAlias("TEST_" + identityNumber);
    SampleTissue tissue = new SampleTissueImpl();
    tissue.setParent(identity);
    tissue.setAlias("TEST_" + identityNumber + "_" + tissueNumber);
    SampleStock stock = new SampleStockImpl();
    stock.setParent(tissue);
    stock.setAlias("TEST_" + identityNumber + "_" + tissueNumber + "_SG01");
    SampleAliquot aliquot = new SampleAliquotImpl();
    aliquot.setParent(stock);
    aliquot.setAlias("TEST_" + identityNumber + "_" + tissueNumber + "_SG01-02");
    DetailedLibrary lib = new DetailedLibraryImpl();
    lib.setSample(aliquot);
    return lib;
  }

  private void mockFirstAvailableSiblingNumber(int siblingNumber) throws IOException {
    Mockito.when(siblingNumberGenerator.getFirstAvailableSiblingNumber(Mockito.any(), Mockito.anyString())).thenReturn(siblingNumber);
  }

}
