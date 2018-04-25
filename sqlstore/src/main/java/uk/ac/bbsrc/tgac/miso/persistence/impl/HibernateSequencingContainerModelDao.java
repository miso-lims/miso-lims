package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.store.SequencingContainerModelStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencingContainerModelDao implements SequencingContainerModelStore {
  protected static final Logger log = LoggerFactory.getLogger(HibernateSequencingContainerModelDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public SequencingContainerModel get(long id) {
    return (SequencingContainerModel) currentSession().get(SequencingContainerModel.class, id);
  }

  @Override
  public SequencingContainerModel find(Platform platform, String search, int partitionCount) {
    SequencingContainerModel model;
    Criteria criteria = currentSession().createCriteria(SequencingContainerModel.class);
    criteria.createAlias("platforms", "platform");
    criteria.add(Restrictions.eq("platform.id", platform.getId()));
    criteria.add(Restrictions.eq("partitionCount", partitionCount));
    if (LimsUtils.isStringEmptyOrNull(search)) {
      criteria.add(Restrictions.eq("fallback", true));
      model = (SequencingContainerModel) criteria.uniqueResult();
    } else {
      criteria.add(Restrictions.or(Restrictions.eq("alias", search), Restrictions.eq("identificationBarcode", search)));
      model = (SequencingContainerModel) criteria.uniqueResult();
      if (model == null) {
        // remove search restriction and get fallback option if search did not retrieve anything
        Criteria fallback = currentSession().createCriteria(SequencingContainerModel.class);
        fallback.createAlias("platforms", "platform");
        fallback.add(Restrictions.eq("platform.id", platform.getId()));
        fallback.add(Restrictions.eq("partitionCount", partitionCount));
        fallback.add(Restrictions.eq("fallback", true));
        model = (SequencingContainerModel) fallback.uniqueResult();
      }
    }
    return model;
  }

  @Override
  public List<SequencingContainerModel> list() {
    Criteria criteria = currentSession().createCriteria(SequencingContainerModel.class);
    @SuppressWarnings("unchecked")
    List<SequencingContainerModel> results = criteria.list();
    return results;
  }

}
