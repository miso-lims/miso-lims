package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;

public interface TissueOriginService {

  public TissueOrigin get(Long tissueOriginId) throws IOException;

  public Set<TissueOrigin> getAll() throws IOException;

  public Long create(TissueOrigin tissueOrigin) throws IOException;

  public void update(TissueOrigin tissueOrigin) throws IOException;

  public void delete(Long tissueOriginId) throws IOException;
}
