package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;

public class V2LibraryAliasGenerator implements NameGenerator<Library> {

  @Autowired
  private SiblingNumberGenerator siblingNumberGenerator;

  public void setSiblingNumberGenerator(SiblingNumberGenerator siblingNumberGenerator) {
    this.siblingNumberGenerator = siblingNumberGenerator;
  }

  @Override
  public String generate(Library library) throws MisoNamingException, IOException {
    if (!isDetailedLibrary(library)) {
      throw new MisoNamingException("Can only generate an alias for detailed libraries");
    }
    DetailedSample tissue = getParent(SampleTissue.class, (DetailedSample) library.getSample());
    String partialAlias = tissue.getAlias() + "_LB";
    int next = siblingNumberGenerator.getFirstAvailableSiblingNumber(LibraryImpl.class, partialAlias);
    String siblingNumber = zeroPad(next, 2);
    return partialAlias + siblingNumber;
  }

}
