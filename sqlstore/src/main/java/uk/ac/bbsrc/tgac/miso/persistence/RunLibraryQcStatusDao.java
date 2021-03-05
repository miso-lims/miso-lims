package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;

public interface RunLibraryQcStatusDao extends SaveDao<RunLibraryQcStatus> {

  RunLibraryQcStatus getByDescription(String description) throws IOException;

  List<RunLibraryQcStatus> listByIdList(Collection<Long> ids) throws IOException;

  long getUsage(RunLibraryQcStatus status) throws IOException;

}
