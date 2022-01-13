package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;

public interface TissueTypeDao extends SaveDao<TissueType> {

  TissueType getByAlias(String alias) throws IOException;

  long getUsage(TissueType tissueType) throws IOException;

  List<TissueType> listByIdList(Collection<Long> ids) throws IOException;

}
