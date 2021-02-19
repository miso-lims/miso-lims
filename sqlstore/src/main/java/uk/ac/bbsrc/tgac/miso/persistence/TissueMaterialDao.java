package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

public interface TissueMaterialDao extends SaveDao<TissueMaterial> {

  long getUsage(TissueMaterial tissueMaterial);

}