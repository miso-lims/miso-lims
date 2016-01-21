package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

public interface TissueMaterialDao {

  List<TissueMaterial> getTissueMaterial();

  TissueMaterial getTissueMaterial(Long id);

  Long addTissueMaterial(TissueMaterial tissueMaterial);

  void deleteTissueMaterial(TissueMaterial tissueMaterial);

  void update(TissueMaterial tissueMaterial);

}