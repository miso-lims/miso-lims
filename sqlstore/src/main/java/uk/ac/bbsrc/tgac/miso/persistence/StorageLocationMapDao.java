package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;

public interface StorageLocationMapDao extends SaveDao<StorageLocationMap> {

  public StorageLocationMap getByFilename(String filename) throws IOException;

  public long getUsage(StorageLocationMap map) throws IOException;

}
