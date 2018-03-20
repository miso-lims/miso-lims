package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.ProgressImpl;
import uk.ac.bbsrc.tgac.miso.core.store.ProgressStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateProgressDao implements ProgressStore {
  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Progress get(long id) {
    return (Progress) currentSession().get(ProgressImpl.class, id);
  }

  @Override
  public List<Progress> listByUserId(long id) {
    @SuppressWarnings("unchecked")
    List<Progress> results = currentSession().createCriteria(ProgressImpl.class).createAlias("user", "u")
        .add(Restrictions.eq("u.userId", id)).list();

    return results;
  }

  @Override
  public Progress save(Progress progress) {
    currentSession().save(progress);

    return progress;
  }
}
