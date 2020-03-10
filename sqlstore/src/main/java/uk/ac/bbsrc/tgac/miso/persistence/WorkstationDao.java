package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Workstation;

public interface WorkstationDao extends SaveDao<Workstation> {

  public Workstation getByAlias(String alias) throws IOException;

  public long getUsage(Workstation workstation) throws IOException;

}
