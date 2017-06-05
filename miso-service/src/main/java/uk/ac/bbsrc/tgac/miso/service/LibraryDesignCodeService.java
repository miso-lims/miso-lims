package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;

public interface LibraryDesignCodeService {

  Collection<LibraryDesignCode> list() throws IOException;

  LibraryDesignCode get(long libraryDesignCodeId) throws IOException;
}
