package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;

public interface StorageLocationMapDao {

  public StorageLocationMap get(long id) throws IOException;

  public StorageLocationMap getByFilename(String filename) throws IOException;

  public List<StorageLocationMap> list() throws IOException;

  public long create(StorageLocationMap map) throws IOException;

  public long update(StorageLocationMap map) throws IOException;

  public long getUsage(StorageLocationMap map) throws IOException;

}
