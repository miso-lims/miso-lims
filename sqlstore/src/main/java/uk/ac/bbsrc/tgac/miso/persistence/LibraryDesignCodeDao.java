package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;

public interface LibraryDesignCodeDao extends SaveDao<LibraryDesignCode> {

  public LibraryDesignCode getByCode(String code) throws IOException;

  public long getUsageByLibraries(LibraryDesignCode code) throws IOException;

  public long getUsageByLibraryDesigns(LibraryDesignCode code) throws IOException;

}
