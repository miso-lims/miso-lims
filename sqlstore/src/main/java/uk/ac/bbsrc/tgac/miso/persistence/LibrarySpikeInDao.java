package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;

public interface LibrarySpikeInDao extends SaveDao<LibrarySpikeIn> {

  public LibrarySpikeIn getByAlias(String alias) throws IOException;

  public long getUsage(LibrarySpikeIn spikeIn) throws IOException;

}
