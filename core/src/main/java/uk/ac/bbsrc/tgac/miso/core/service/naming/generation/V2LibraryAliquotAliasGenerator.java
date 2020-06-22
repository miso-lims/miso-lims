package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;

public class V2LibraryAliquotAliasGenerator implements NameGenerator<LibraryAliquot> {

  @Autowired
  private SiblingNumberGenerator siblingNumberGenerator;

  public void setSiblingNumberGenerator(SiblingNumberGenerator siblingNumberGenerator) {
    this.siblingNumberGenerator = siblingNumberGenerator;
  }

  @Override
  public String generate(LibraryAliquot aliquot) throws MisoNamingException, IOException {
    if (!isDetailedLibraryAliquot(aliquot)) {
      throw new MisoNamingException("Can only generate an alias for detailed library aliquots");
    }
    Library library = aliquot.getLibrary();
    String partialAlias = library.getAlias() + "-";
    int next = siblingNumberGenerator.getFirstAvailableSiblingNumber(LibraryAliquot.class, partialAlias);
    String siblingNumber = zeroPad(next, 2);
    return partialAlias + siblingNumber;
  }

}
