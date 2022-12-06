package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;

public interface LibrarySpikeInDao extends SaveDao<LibrarySpikeIn> {

  LibrarySpikeIn getByAlias(String alias) throws IOException;

  long getUsage(LibrarySpikeIn spikeIn) throws IOException;

  List<LibrarySpikeIn> listByIdList(List<Long> idList) throws IOException;

}
