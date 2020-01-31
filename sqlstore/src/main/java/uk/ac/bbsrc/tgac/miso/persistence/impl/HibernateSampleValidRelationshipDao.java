package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleValidRelationshipDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleValidRelationshipDao implements SampleValidRelationshipDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleValidRelationshipDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SampleValidRelationship> list() {
    @SuppressWarnings("unchecked")
    List<SampleValidRelationship> records = currentSession().createCriteria(SampleValidRelationshipImpl.class).list();
    return records;
  }

  @Override
  public SampleValidRelationship get(Long id) {
    return (SampleValidRelationship) currentSession().get(SampleValidRelationshipImpl.class, id);
  }

  @Override
  public SampleValidRelationship getByClasses(SampleClass parent, SampleClass child) throws IOException {
    return (SampleValidRelationship) currentSession().createCriteria(SampleValidRelationshipImpl.class)
        .add(Restrictions.eq("parent", parent))
        .add(Restrictions.eq("child", child))
        .uniqueResult();
  }

  @Override
  public Long create(SampleValidRelationship sampleValidRelationship) {
    Date now = new Date();
    sampleValidRelationship.setCreationTime(now);
    sampleValidRelationship.setLastModified(now);
    return (Long) currentSession().save(sampleValidRelationship);
  }

  @Override
  public void update(SampleValidRelationship sampleValidRelationship) {
    Date now = new Date();
    sampleValidRelationship.setLastModified(now);
    currentSession().update(sampleValidRelationship);
  }

  @Override
  public void delete(SampleValidRelationship sampleValidRelationship) throws IOException {
    currentSession().delete(sampleValidRelationship);
  }

}
