package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.RunItemQcStatus;

public interface RunItemQcStatusDao extends BulkSaveDao<RunItemQcStatus> {

  RunItemQcStatus getByDescription(String description) throws IOException;

  long getUsage(RunItemQcStatus status) throws IOException;

}
