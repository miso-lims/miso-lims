package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface DetailedQcStatusService extends DeleterService<DetailedQcStatus>, SaveService<DetailedQcStatus> {

  Set<DetailedQcStatus> getAll() throws IOException;

}