package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;

public interface TissueOriginService {

  public TissueOrigin get(Long tissueOriginId);

  public Set<TissueOrigin> getAll();

  public Long create(TissueOrigin tissueOrigin) throws IOException;

  public void update(TissueOrigin tissueOrigin) throws IOException;

  public void delete(Long tissueOriginId);
}
