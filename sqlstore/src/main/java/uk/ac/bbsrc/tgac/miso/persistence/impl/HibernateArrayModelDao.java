package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel_;
import uk.ac.bbsrc.tgac.miso.core.data.Array_;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayModelDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateArrayModelDao extends HibernateSaveDao<ArrayModel> implements ArrayModelDao {

  public HibernateArrayModelDao() {
    super(ArrayModel.class);
  }

  @Override
  public ArrayModel getByAlias(String alias) throws IOException {
    return getBy(ArrayModel_.ALIAS, alias);
  }

  @Override
  public List<ArrayModel> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(ArrayModel_.ID, idList);
  }

  @Override
  public long getUsage(ArrayModel model) throws IOException {
    return getUsageBy(Array.class, Array_.ARRAY_MODEL, model);
  }

}
