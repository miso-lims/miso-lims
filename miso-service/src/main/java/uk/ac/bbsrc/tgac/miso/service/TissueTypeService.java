package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;

public interface TissueTypeService {

  public TissueType get(Long tissueTypeId) throws IOException;

  public Set<TissueType> getAll() throws IOException;

  public Long create(TissueType tissueType) throws IOException;

  public void update(TissueType tissueType) throws IOException;

  public void delete(Long tissueTypeId) throws IOException;
}
