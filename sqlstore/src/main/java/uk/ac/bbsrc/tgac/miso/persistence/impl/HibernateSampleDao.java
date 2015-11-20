package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;

@Repository
@Transactional
public class HibernateSampleDao implements SampleDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<Sample> getSample() {
    Query query = currentSession().createQuery("from AbstractSample");
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return records;
  }

  @Override
  public Sample getSample(Long id) {
    return (Sample) currentSession().get(SampleImpl.class, id);
  }

  @Override
  public Long addSample(Sample sample) {
    return (Long) currentSession().save(sample);
  }

  @Override
  public void deleteSample(Sample sample) {
    currentSession().delete(sample);

  }

  @Override
  public void update(Sample sample) {
    currentSession().update(sample);
  }

}
