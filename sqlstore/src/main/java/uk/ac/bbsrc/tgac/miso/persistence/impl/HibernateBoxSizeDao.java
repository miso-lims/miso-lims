package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.persistence.BoxSizeDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBoxSizeDao extends HibernateSaveDao<BoxSize> implements BoxSizeDao {

  public HibernateBoxSizeDao() {
    super(BoxSize.class);
  }

  @Override
  public long getUsage(BoxSize boxSize) {
    return getUsageBy(BoxImpl.class, "size", boxSize);
  }

  @Override
  public List<BoxSize> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("id", idList);
  }

}
