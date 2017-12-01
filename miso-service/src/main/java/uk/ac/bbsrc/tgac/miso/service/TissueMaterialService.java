package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

public interface TissueMaterialService {

  TissueMaterial get(Long tissueMaterialId) throws IOException;

  Long create(TissueMaterial tissueMaterial) throws IOException;

  void update(TissueMaterial tissueMaterial) throws IOException;

  Set<TissueMaterial> getAll() throws IOException;

  void delete(Long tissueMaterialId) throws IOException;

}