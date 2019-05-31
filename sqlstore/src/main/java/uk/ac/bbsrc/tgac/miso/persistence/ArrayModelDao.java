package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;

public interface ArrayModelDao {

  public ArrayModel get(long id) throws IOException;

  public ArrayModel getByAlias(String alias) throws IOException;

  public List<ArrayModel> list() throws IOException;

  public long create(ArrayModel model) throws IOException;

  public long update(ArrayModel model) throws IOException;

  public long getUsage(ArrayModel model) throws IOException;

}
