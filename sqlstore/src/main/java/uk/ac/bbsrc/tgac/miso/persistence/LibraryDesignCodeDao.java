package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;

public interface LibraryDesignCodeDao extends BulkSaveDao<LibraryDesignCode> {

  LibraryDesignCode getByCode(String code) throws IOException;

  long getUsageByLibraries(LibraryDesignCode code) throws IOException;

  long getUsageByLibraryDesigns(LibraryDesignCode code) throws IOException;

}
