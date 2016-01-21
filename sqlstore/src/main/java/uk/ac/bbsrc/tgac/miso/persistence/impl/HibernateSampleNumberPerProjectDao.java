package uk.ac.bbsrc.tgac.miso.persistence.impl;

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

import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleNumberPerProjectDao;

@Repository
@Transactional
public class HibernateSampleNumberPerProjectDao implements SampleNumberPerProjectDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleNumberPerProjectDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SampleNumberPerProject> getSampleNumberPerProject() {
    Query query = currentSession().createQuery("from SampleNumberPerProjectImpl");
    @SuppressWarnings("unchecked")
    List<SampleNumberPerProject> records = query.list();
    return records;
  }

  @Override
  public SampleNumberPerProject getSampleNumberPerProject(Long id) {
    return (SampleNumberPerProject) currentSession().get(SampleNumberPerProjectImpl.class, id);
  }

  @Override
  public Long addSampleNumberPerProject(SampleNumberPerProject sampleNumberPerProject) {
    Date now = new Date();
    sampleNumberPerProject.setCreationDate(now);
    sampleNumberPerProject.setLastUpdated(now);
    return (Long) currentSession().save(sampleNumberPerProject);
  }

  @Override
  public void deleteSampleNumberPerProject(SampleNumberPerProject sampleNumberPerProject) {
    currentSession().delete(sampleNumberPerProject);

  }

  @Override
  public void update(SampleNumberPerProject sampleNumberPerProject) {
    Date now = new Date();
    sampleNumberPerProject.setLastUpdated(now);
    currentSession().update(sampleNumberPerProject);
  }

}
