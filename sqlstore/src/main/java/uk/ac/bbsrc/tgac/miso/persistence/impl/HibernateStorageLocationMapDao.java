package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation_;
import uk.ac.bbsrc.tgac.miso.persistence.StorageLocationMapDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStorageLocationMapDao extends HibernateSaveDao<StorageLocationMap>
    implements StorageLocationMapDao {

  public HibernateStorageLocationMapDao() {
    super(StorageLocationMap.class);
  }

  @Override
  public StorageLocationMap getByFilename(String filename) throws IOException {
    return getBy(StorageLocationMap_.filename, filename);
  }

  @Override
  public long getUsage(StorageLocationMap map) throws IOException {
    return getUsageBy(StorageLocation.class, StorageLocation_.map, map);
  }

}
