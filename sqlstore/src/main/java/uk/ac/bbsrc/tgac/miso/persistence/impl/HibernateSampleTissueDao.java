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

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleTissueDao;

@Repository
@Transactional
public class HibernateSampleTissueDao implements SampleTissueDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleTissueDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SampleTissue> getSampleTissue() {
    Query query = currentSession().createQuery("from SampleTissueImpl");
    @SuppressWarnings("unchecked")
    List<SampleTissue> records = query.list();
    return records;
  }

  @Override
  public SampleTissue getSampleTissue(Long id) {
    return (SampleTissue) currentSession().get(SampleTissueImpl.class, id);
  }

  @Override
  public void deleteSampleTissue(SampleTissue sampleTissue) {
    currentSession().delete(sampleTissue);
  }

}
