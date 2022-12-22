package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.persistence.BoxUseDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBoxUseDao extends HibernateSaveDao<BoxUse> implements BoxUseDao {

  public HibernateBoxUseDao() {
    super(BoxUse.class);
  }

  @Override
  public BoxUse getByAlias(String alias) {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(BoxUse boxUse) {
    return getUsageBy(BoxImpl.class, "use", boxUse);
  }

  @Override
  public List<BoxUse> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("id", idList);
  }

}
