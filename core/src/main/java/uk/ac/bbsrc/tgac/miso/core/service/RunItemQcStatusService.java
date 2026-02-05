package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.RunItemQcStatus;

public interface RunItemQcStatusService
    extends BulkSaveService<RunItemQcStatus>, DeleterService<RunItemQcStatus> {

  List<RunItemQcStatus> list() throws IOException;

  RunItemQcStatus getByDescription(String description) throws IOException;

}
