package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTissueTypeDao implements TissueTypeDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateTissueTypeDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<TissueType> list() {
    Query query = currentSession().createQuery("from TissueTypeImpl");
    @SuppressWarnings("unchecked")
    List<TissueType> records = query.list();
    return records;
  }

  @Override
  public TissueType get(Long id) {
    return (TissueType) currentSession().get(TissueTypeImpl.class, id);
  }

  @Override
  public TissueType getByAlias(String alias) {
    return (TissueType) currentSession().createCriteria(TissueTypeImpl.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public Long create(TissueType tissueType) {
    return (Long) currentSession().save(tissueType);
  }

  @Override
  public long update(TissueType tissueType) {
    currentSession().update(tissueType);
    return tissueType.getId();
  }

  @Override
  public long getUsage(TissueType tissueType) {
    return (long) currentSession().createCriteria(SampleTissueImpl.class)
        .add(Restrictions.eq("tissueType", tissueType))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
