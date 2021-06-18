package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLabel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.persistence.StorageLabelDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStorageLabelDao extends HibernateSaveDao<StorageLabel> implements StorageLabelDao {

  public HibernateStorageLabelDao() {
    super(StorageLabel.class);
  }

  @Override
  public StorageLabel getByLabel(String label) throws IOException {
    return getBy("label", label);
  }

  @Override
  public long getUsage(StorageLabel label) throws IOException {
    return getUsageBy(StorageLocation.class, "label", label);
  }

  @Override
  public List<StorageLabel> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("labelId", ids);
  }

}
