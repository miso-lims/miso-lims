package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;

@Repository
@Transactional
public class HibernateSampleClassDao implements SampleClassDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleClassDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SampleClass> getSampleClass() {
    Query query = currentSession().createQuery("from SampleClassImpl");
    @SuppressWarnings("unchecked")
    List<SampleClass> records = query.list();
    return records;
  }

  @Override
  public SampleClass getSampleClass(Long id) {
    return (SampleClass) currentSession().get(SampleClassImpl.class, id);
  }

  @Override
  public Long addSampleClass(SampleClass sampleClass) {
    Date now = new Date();
    sampleClass.setCreationDate(now);
    sampleClass.setLastUpdated(now);
    return (Long) currentSession().save(sampleClass);
  }

  @Override
  public void deleteSampleClass(SampleClass sampleClass) {
    currentSession().delete(sampleClass);

  }

  @Override
  public void update(SampleClass sampleClass) {
    Date now = new Date();
    sampleClass.setLastUpdated(now);
    currentSession().update(sampleClass);
  }

}
