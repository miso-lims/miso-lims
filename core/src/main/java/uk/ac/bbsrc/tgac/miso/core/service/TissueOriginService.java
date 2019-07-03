package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;

public interface TissueOriginService extends DeleterService<TissueOrigin>, ListService<TissueOrigin> {

  public Long create(TissueOrigin tissueOrigin) throws IOException;

  public void update(TissueOrigin tissueOrigin) throws IOException;

}
