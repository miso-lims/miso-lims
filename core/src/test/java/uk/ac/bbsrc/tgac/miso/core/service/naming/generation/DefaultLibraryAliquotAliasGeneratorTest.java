package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public class DefaultLibraryAliquotAliasGeneratorTest {

  private DefaultLibraryAliquotAliasGenerator sut;

  @Before
  public void setup() {
    sut = new DefaultLibraryAliquotAliasGenerator();
  }

  @Test
  public void testGenerate() throws MisoNamingException, IOException {
    String alias = "TEST_ALIAS_123";
    Library lib = new LibraryImpl();
    lib.setAlias(alias);
    LibraryAliquot aliquot = new LibraryAliquot();
    aliquot.setLibrary(lib);
    assertEquals(alias, sut.generate(aliquot));
  }

}
