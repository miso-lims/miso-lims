package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;

public interface LibrarySpikeInDao extends BulkSaveDao<LibrarySpikeIn> {

  LibrarySpikeIn getByAlias(String alias) throws IOException;

  long getUsage(LibrarySpikeIn spikeIn) throws IOException;

}
