package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;

public interface ArrayModelDao extends ProviderDao<ArrayModel>, BulkSaveDao<ArrayModel> {

  ArrayModel getByAlias(String alias) throws IOException;

  long getUsage(ArrayModel model) throws IOException;

}
