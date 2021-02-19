package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;

public interface TissueOriginDao extends SaveDao<TissueOrigin> {

  public TissueOrigin getByAlias(String alias);

  public long getUsage(TissueOrigin tissueOrigin);

}
