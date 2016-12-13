package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

public class DefaultLibraryAliasGenerator implements NameGenerator<Library> {

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @Override
  public String generate(Library library) throws MisoNamingException {
    if (library.getSample() != null) {
      Pattern samplePattern = Pattern.compile("([A-z0-9]+)_S([A-z0-9]+)_(.*)");
      Matcher m = samplePattern.matcher(library.getSample().getAlias());

      if (m.matches()) {
        try {
          int numLibs = requestManager.listAllLibrariesBySampleId(library.getSample().getId()).size();
          String la = m.group(1) + "_" + "L" + m.group(2) + "-" + (numLibs + 1) + "_" + m.group(3);
          return la;
        } catch (IOException e) {
          throw new MisoNamingException("Cannot generate Library alias for: " + library.toString(), e);
        }
      } else {
        throw new MisoNamingException(
            "Cannot generate Library alias for: " + library.toString() + " from supplied sample alias: " + library.getSample().getAlias());
      }
    } else {
      throw new MisoNamingException("This alias generation scheme requires the Library to have a parent Sample set.");
    }
  }

}
