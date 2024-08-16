package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;

public interface ArrayModelDao extends BulkSaveDao<ArrayModel> {

  ArrayModel getByAlias(String alias) throws IOException;

  long getUsage(ArrayModel model) throws IOException;

}
