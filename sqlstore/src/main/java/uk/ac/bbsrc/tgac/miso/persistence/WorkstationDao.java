package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Workstation;

public interface WorkstationDao extends BulkSaveDao<Workstation> {

  Workstation getByAlias(String alias) throws IOException;

  long getUsage(Workstation workstation) throws IOException;

}
