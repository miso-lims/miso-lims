package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.persistence.IndexFamilyDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateIndexFamilyDao extends HibernateSaveDao<IndexFamily> implements IndexFamilyDao {

  public HibernateIndexFamilyDao() {
    super(IndexFamily.class);
  }

  @Override
  public IndexFamily getByName(String name) throws IOException {
    return getBy("name", name);
  }

  @Override
  public long getUsage(IndexFamily indexFamily) throws IOException {
    return (long) currentSession().createCriteria(LibraryImpl.class)
        .createAlias("indices", "index")
        .createAlias("index.family", "indexFamily")
        .add(Restrictions.eq("indexFamily.id", indexFamily.getId()))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

}
