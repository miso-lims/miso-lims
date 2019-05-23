package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleTypeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleTypeDao implements SampleTypeDao {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public SampleType get(long id) throws IOException {
    return (SampleType) currentSession().get(SampleType.class, id);
  }

  @Override
  public SampleType getByName(String name) throws IOException {
    return (SampleType) currentSession().createCriteria(SampleType.class)
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public List<SampleType> list() throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleType.class);
    @SuppressWarnings("unchecked")
    List<SampleType> results = criteria.list();
    return results;
  }

  @Override
  public long create(SampleType sampleType) throws IOException {
    return (long) currentSession().save(sampleType);
  }

  @Override
  public long update(SampleType sampleType) throws IOException {
    currentSession().update(sampleType);
    return sampleType.getId();
  }

  @Override
  public long getUsage(SampleType sampleType) throws IOException {
    return (long) currentSession().createCriteria(SampleImpl.class)
        .add(Restrictions.eq("sampleType", sampleType.getName()))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
