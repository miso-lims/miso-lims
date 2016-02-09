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

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;

@Repository
@Transactional
public class HibernateTissueTypeDao implements TissueTypeDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateTissueTypeDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<TissueType> getTissueType() {
    Query query = currentSession().createQuery("from TissueTypeImpl");
    @SuppressWarnings("unchecked")
    List<TissueType> records = query.list();
    return records;
  }

  @Override
  public TissueType getTissueType(Long id) {
    return (TissueType) currentSession().get(TissueTypeImpl.class, id);
  }

  @Override
  public Long addTissueType(TissueType tissueType) {
    Date now = new Date();
    tissueType.setCreationDate(now);
    tissueType.setLastUpdated(now);
    return (Long) currentSession().save(tissueType);
  }

  @Override
  public void deleteTissueType(TissueType tissueType) {
    currentSession().delete(tissueType);

  }

  @Override
  public void update(TissueType tissueType) {
    Date now = new Date();
    tissueType.setLastUpdated(now);
    currentSession().update(tissueType);
  }

}
