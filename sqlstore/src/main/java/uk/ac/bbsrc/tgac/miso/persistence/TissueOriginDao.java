package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface TissueOriginDao extends BulkSaveDao<TissueOrigin> {

  TissueOrigin getByAlias(String alias) throws IOException;

  long getUsage(TissueOrigin tissueOrigin) throws IOException;

}
