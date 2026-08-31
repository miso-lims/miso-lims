package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public class DefaultLibraryAliquotAliasGeneratorTest {

  private DefaultLibraryAliquotAliasGenerator sut;

  @BeforeEach
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
