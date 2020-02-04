package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;

public class V2LibraryAliquotAliasGeneratorTest {

  @Mock
  private SiblingNumberGenerator siblingNumberGenerator;

  @InjectMocks
  private V2LibraryAliquotAliasGenerator sut;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGenerate() throws Exception {
    testGenerate("TEST_0123_04_LB05", 6, "TEST_0123_04_LB05-06");
  }

  @Test
  public void testGenerateForShortIdentityNumber() throws Exception {
    testGenerate("TEST_123_02_LB04", 8, "TEST_123_02_LB04-08");
  }

  @Test
  public void testGenerateForLongNumbers() throws Exception {
    testGenerate("TEST_123456_789_LB123", 456, "TEST_123456_789_LB123-456");
  }

  private void testGenerate(String libraryAlias, int siblingNumber, String expectedResult) throws IOException, MisoNamingException {
    LibraryAliquot aliquot = makeAliquot(libraryAlias);
    mockFirstAvailableSiblingNumber(siblingNumber);
    assertEquals(expectedResult, sut.generate(aliquot));
  }

  private static LibraryAliquot makeAliquot(String libraryAlias) {
    Library lib = new DetailedLibraryImpl();
    lib.setAlias(libraryAlias);
    LibraryAliquot aliquot = new DetailedLibraryAliquot();
    aliquot.setLibrary(lib);
    return aliquot;
  }

  private void mockFirstAvailableSiblingNumber(int siblingNumber) throws IOException {
    Mockito.when(siblingNumberGenerator.getFirstAvailableSiblingNumber(Mockito.any(), Mockito.anyString())).thenReturn(siblingNumber);
  }

}
