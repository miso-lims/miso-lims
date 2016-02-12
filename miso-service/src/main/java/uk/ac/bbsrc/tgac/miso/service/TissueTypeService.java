package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;

public interface TissueTypeService {

  public TissueType get(Long tissueTypeId);

  public Set<TissueType> getAll();

  public Long create(TissueType tissueType) throws IOException;

  public void update(TissueType tissueType) throws IOException;

  public void delete(Long tissueTypeId);
}
