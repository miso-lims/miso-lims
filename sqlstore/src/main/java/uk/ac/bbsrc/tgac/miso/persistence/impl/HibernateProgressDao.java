package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import javax.persistence.criteria.Join;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.ProgressImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.ProgressImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.ProgressStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateProgressDao implements ProgressStore {
  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Progress get(long id) {
    Progress progress = (Progress) currentSession().get(ProgressImpl.class, id);
    if (progress != null) {
      currentSession().evict(progress);
      for (ProgressStep step : progress.getSteps()) {
        currentSession().evict(step);
      }
    }
    return progress;
  }

  @Override
  public Progress getManaged(long id) {
    return (Progress) currentSession().get(ProgressImpl.class, id);
  }

  @Override
  public List<Progress> listByUserId(long id) {
    QueryBuilder<?, ProgressImpl> builder =
        new QueryBuilder<>(currentSession(), ProgressImpl.class, Object[].class, Criteria.DISTINCT_ROOT_ENTITY);
    Join<ProgressImpl, UserImpl> userJoin = builder.getJoin(builder.getRoot(), ProgressImpl_.user);
    builder.addPredicate(builder.getCriteriaBuilder().equal(userJoin.get(UserImpl_.userId), id));
    builder.setColumns(builder.getRoot());

    @SuppressWarnings("unchecked")
    List<Progress> results = (List<Progress>) builder.getResultList();
    return results;
  }

  @Override
  public Progress save(Progress progress) {
    if (!progress.isSaved()) {
      currentSession().save(progress);
    } else {
      currentSession().update(progress);
    }

    if (progress.getSteps() != null) {
      for (ProgressStep step : progress.getSteps()) {
        currentSession().saveOrUpdate(step);
      }
    }

    return progress;
  }

  @Override
  public void delete(Progress progress) {
    progress.getSteps().forEach(this::delete);
    currentSession().delete(progress);
  }

  @Override
  public void delete(ProgressStep step) {
    currentSession().delete(step);
    currentSession().flush();
  }

}
