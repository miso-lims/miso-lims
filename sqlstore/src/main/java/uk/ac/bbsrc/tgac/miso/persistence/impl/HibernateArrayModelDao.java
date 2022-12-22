package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayModelDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateArrayModelDao extends HibernateSaveDao<ArrayModel> implements ArrayModelDao {

  public HibernateArrayModelDao() {
    super(ArrayModel.class);
  }

  @Override
  public ArrayModel getByAlias(String alias) throws IOException {
    return (ArrayModel) currentSession().createCriteria(ArrayModel.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public List<ArrayModel> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("id", idList);
  }

  @Override
  public List<ArrayModel> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<ArrayModel> results = currentSession().createCriteria(ArrayModel.class).list();
    return results;
  }

  @Override
  public long getUsage(ArrayModel model) throws IOException {
    return (long) currentSession().createCriteria(Array.class)
        .add(Restrictions.eq("arrayModel", model))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
