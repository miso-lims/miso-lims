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

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;

@Repository
@Transactional
public class HibernateTissueOriginDao implements TissueOriginDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateTissueOriginDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<TissueOrigin> getTissueOrigin() {
    Query query = currentSession().createQuery("from TissueOriginImpl");
    @SuppressWarnings("unchecked")
    List<TissueOrigin> records = query.list();
    return records;
  }

  @Override
  public TissueOrigin getTissueOrigin(Long id) {
    return (TissueOrigin) currentSession().get(TissueOriginImpl.class, id);
  }

  @Override
  public Long addTissueOrigin(TissueOrigin tissueOrigin) {
    Date now = new Date();
    tissueOrigin.setCreationDate(now);
    tissueOrigin.setLastUpdated(now);
    log.error("tissue orgin from hibernate:" + tissueOrigin);
    return (Long) currentSession().save(tissueOrigin);
  }

  @Override
  public void deleteTissueOrigin(TissueOrigin tissueOrigin) {
    currentSession().delete(tissueOrigin);

  }

  @Override
  public void update(TissueOrigin tissueOrigin) {
    Date now = new Date();
    tissueOrigin.setLastUpdated(now);
    log.error("tissue orgin from hibernate during update:" + tissueOrigin);
    currentSession().update(tissueOrigin);
  }

}
