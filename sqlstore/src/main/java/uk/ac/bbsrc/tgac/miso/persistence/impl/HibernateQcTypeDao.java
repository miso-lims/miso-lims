package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.store.QualityControlTypeStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateQcTypeDao implements QualityControlTypeStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateQcTypeDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }
  @Override
  public QcType get(long id) throws IOException {
    return (QcType) currentSession().get(QcType.class, id);
  }


  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public Collection<QcType> list() throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    @SuppressWarnings("unchecked")
    List<QcType> records = criteria.list();
    return records;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
