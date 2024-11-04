package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;

public interface TissueOriginDao extends BulkSaveDao<TissueOrigin> {

  TissueOrigin getByAlias(String alias) throws IOException;

  long getUsage(TissueOrigin tissueOrigin) throws IOException;

}
