package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;
import uk.ac.bbsrc.tgac.miso.persistence.StainDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStainDao extends HibernateSaveDao<Stain> implements StainDao {

  public HibernateStainDao() {
    super(Stain.class);
  }

  @Override
  public Stain getByName(String name) throws IOException {
    return (Stain) currentSession().createCriteria(Stain.class)
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public long getUsage(Stain stain) throws IOException {
    return (long) currentSession().createCriteria(SampleSlideImpl.class)
        .add(Restrictions.eq("stain", stain))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public List<Stain> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("stainId", ids);
  }

}
