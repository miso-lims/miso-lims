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

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.core.data.impl.QcPassedDetailImpl;
import uk.ac.bbsrc.tgac.miso.persistence.QcPassedDetailDao;

@Repository
@Transactional
public class HibernateQcPassedDetailDao implements QcPassedDetailDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateQcPassedDetailDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<QcPassedDetail> getQcPassedDetails() {
    Query query = currentSession().createQuery("from QcPassedDetailsImpl");
    @SuppressWarnings("unchecked")
    List<QcPassedDetail> records = query.list();
    return records;
  }

  @Override
  public QcPassedDetail getQcPassedDetails(Long id) {
    return (QcPassedDetail) currentSession().get(QcPassedDetailImpl.class, id);
  }

  @Override
  public Long addQcPassedDetails(QcPassedDetail qcPassedDetail) {
    Date now = new Date();
    qcPassedDetail.setCreationDate(now);
    qcPassedDetail.setLastUpdated(now);
    return (Long) currentSession().save(qcPassedDetail);
  }

  @Override
  public void deleteQcPassedDetails(QcPassedDetail qcPassedDetail) {
    currentSession().delete(qcPassedDetail);

  }

  @Override
  public void update(QcPassedDetail qcPassedDetail) {
    Date now = new Date();
    qcPassedDetail.setLastUpdated(now);
    currentSession().update(qcPassedDetail);
  }

}
