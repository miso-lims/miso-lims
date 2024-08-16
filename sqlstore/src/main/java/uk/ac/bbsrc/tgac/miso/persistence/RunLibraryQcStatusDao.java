package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;

public interface RunLibraryQcStatusDao extends BulkSaveDao<RunLibraryQcStatus> {

  RunLibraryQcStatus getByDescription(String description) throws IOException;

  long getUsage(RunLibraryQcStatus status) throws IOException;

}
