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

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;

@Repository
@Transactional
public class HibernateTissueMaterialDao implements TissueMaterialDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateTissueMaterialDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<TissueMaterial> getTissueMaterial() {
    Query query = currentSession().createQuery("from TissueMaterialImpl");
    @SuppressWarnings("unchecked")
    List<TissueMaterial> records = query.list();
    return records;
  }

  @Override
  public TissueMaterial getTissueMaterial(Long id) {
    return (TissueMaterial) currentSession().get(TissueMaterialImpl.class, id);
  }

  @Override
  public Long addTissueMaterial(TissueMaterial tissueMaterial) {
    Date now = new Date();
    tissueMaterial.setCreationDate(now);
    tissueMaterial.setLastUpdated(now);
    return (Long) currentSession().save(tissueMaterial);
  }

  @Override
  public void deleteTissueMaterial(TissueMaterial tissueMaterial) {
    currentSession().delete(tissueMaterial);

  }

  @Override
  public void update(TissueMaterial tissueMaterial) {
    Date now = new Date();
    tissueMaterial.setLastUpdated(now);
    currentSession().update(tissueMaterial);
  }

}
