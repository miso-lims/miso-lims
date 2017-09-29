package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;

public interface LibraryDesignCodeDao {

  List<LibraryDesignCode> getLibraryDesignCodes() throws IOException;

  LibraryDesignCode getLibraryDesignCode(Long id) throws IOException;
}
