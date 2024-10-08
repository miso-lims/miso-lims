package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.ProgressImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.ProgressImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.ProgressStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateProgressDao implements ProgressStore {
  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
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
    QueryBuilder<Progress, ProgressImpl> builder =
        new QueryBuilder<>(currentSession(), ProgressImpl.class, Progress.class);
    Join<ProgressImpl, UserImpl> userJoin = builder.getJoin(builder.getRoot(), ProgressImpl_.user);
    builder.addPredicate(builder.getCriteriaBuilder().equal(userJoin.get(UserImpl_.userId), id));

    List<Progress> results = builder.getResultList();
    return results;
  }

  @Override
  public Progress save(Progress progress) {
    if (!progress.isSaved()) {
      currentSession().persist(progress);
    }

    if (progress.getSteps() != null) {
      for (ProgressStep step : progress.getSteps()) {
        if (currentSession().get(AbstractProgressStep.class, step.getId()) == null) {
          currentSession().persist(step);
        } else {
          currentSession().merge(step);
        }
      }
    }
    currentSession().merge(progress);

    return progress;
  }

  @Override
  public void delete(Progress progress) {
    progress.getSteps().forEach(this::delete);
    currentSession().remove(progress);
  }

  @Override
  public void delete(ProgressStep step) {
    currentSession().remove(step);
    currentSession().flush();
  }

}
