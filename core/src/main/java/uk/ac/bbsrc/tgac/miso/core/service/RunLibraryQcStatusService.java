package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;

public interface RunLibraryQcStatusService
    extends BulkSaveService<RunLibraryQcStatus>, DeleterService<RunLibraryQcStatus> {

  List<RunLibraryQcStatus> list() throws IOException;

  RunLibraryQcStatus getByDescription(String description) throws IOException;

}
