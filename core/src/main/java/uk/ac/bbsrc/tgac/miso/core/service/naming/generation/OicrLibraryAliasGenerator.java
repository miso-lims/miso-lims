package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

// TODO: fix/finish - this is the alias generation code as it was (not working) before naming scheme refactor (GLT-551)
public class OicrLibraryAliasGenerator implements NameGenerator<Library> {

  private static final String sampleRegex = "^([A-Z\\d]{3,5}_\\d{3,5}_(?:[A-Z][a-z]|nn)_[A-Zn])_.*";
  private static final Pattern samplePattern = Pattern.compile(sampleRegex);

  @Override
  public String generate(Library library) throws MisoNamingException {
    if (library.getSample() != null) {
      Matcher m = samplePattern.matcher(library.getSample().getAlias());

      if (m.matches()) {
        // try {
        // int numLibs = requestManager.listAllLibrariesBySampleId(l.getSample().getId()).size();
        String libraryType = "??";
        if (library.getLibraryType().getDescription().equals("Paired End")) {
          libraryType = "PE";
        } else if (library.getLibraryType().getDescription().equals("Single End")) {
          libraryType = "SE";
        }
        String libraryDesignCode = library.getLibraryAdditionalInfo().getLibraryDesignCode().getCode();
        String estimateInsertSize = "300";
        StringBuilder sb = new StringBuilder();
        sb.append(m.group(1)); // PCSI_0123_Pa_R (project name, patient number, tissue origin, tissue type)
        sb.append("_").append(libraryType); // PE (library type)
        sb.append("_").append(estimateInsertSize); // 300 (estimated insert size)
        sb.append("_").append(libraryDesignCode); // WG (source template type)
        String libraryAlias = sb.toString();
        // String la = m.group(1) + "_" + m.group(2) + "-" + (numLibs + 1) + "_" + m.group(3);
        return libraryAlias;
        // } catch (IOException e) {
        // throw new MisoNamingException("Cannot generate Library alias for: " + l.toString(), e);
        // }
      } else {
        throw new MisoNamingException("Cannot generate Library alias for: " + library.toString() + " from supplied sample alias: "
            + library.getSample().getAlias());
      }
    } else {
      throw new MisoNamingException("This alias generation scheme requires the Library to have a parent Sample set.");
    }
  }

}
