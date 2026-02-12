package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;

public interface ProbeSetService extends BulkSaveService<ProbeSet>, DeleterService<ProbeSet>, ListService<ProbeSet> {

  List<ProbeSet> searchByName(String name) throws IOException;

}
