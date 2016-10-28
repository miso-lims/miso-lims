package uk.ac.bbsrc.tgac.miso.core.store;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;

public interface LibraryDesignCodeDao {

  List<LibraryDesignCode> getLibraryDesignCodes();

  LibraryDesignCode getLibraryDesignCode(Long id);
}
