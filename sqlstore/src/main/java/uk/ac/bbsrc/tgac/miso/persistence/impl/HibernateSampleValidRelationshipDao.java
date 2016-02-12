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

import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleValidRelationshipDao;

@Repository
@Transactional
public class HibernateSampleValidRelationshipDao implements SampleValidRelationshipDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleValidRelationshipDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SampleValidRelationship> getSampleValidRelationship() {
    Query query = currentSession().createQuery("from SampleValidRelationshipImpl");
    @SuppressWarnings("unchecked")
    List<SampleValidRelationship> records = query.list();
    return records;
  }

  @Override
  public SampleValidRelationship getSampleValidRelationship(Long id) {
    return (SampleValidRelationship) currentSession().get(SampleValidRelationshipImpl.class, id);
  }

  @Override
  public Long addSampleValidRelationship(SampleValidRelationship sampleValidRelationship) {
    Date now = new Date();
    sampleValidRelationship.setCreationDate(now);
    sampleValidRelationship.setLastUpdated(now);
    return (Long) currentSession().save(sampleValidRelationship);
  }

  @Override
  public void deleteSampleValidRelationship(SampleValidRelationship sampleValidRelationship) {
    currentSession().delete(sampleValidRelationship);

  }

  @Override
  public void update(SampleValidRelationship sampleValidRelationship) {
    Date now = new Date();
    sampleValidRelationship.setLastUpdated(now);
    currentSession().update(sampleValidRelationship);
  }

}
