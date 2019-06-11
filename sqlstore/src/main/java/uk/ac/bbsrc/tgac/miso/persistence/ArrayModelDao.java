package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.store.ProviderDao;
import uk.ac.bbsrc.tgac.miso.core.store.SaveDao;

public interface ArrayModelDao extends ProviderDao<ArrayModel>, SaveDao<ArrayModel> {

  public ArrayModel getByAlias(String alias) throws IOException;

  public long getUsage(ArrayModel model) throws IOException;

}
