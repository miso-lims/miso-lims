package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;

public interface TissueOriginService extends DeleterService<TissueOrigin> {

  public Set<TissueOrigin> getAll() throws IOException;

  public Long create(TissueOrigin tissueOrigin) throws IOException;

  public void update(TissueOrigin tissueOrigin) throws IOException;

}
