package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSubprojectDao implements SubprojectDao {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<Subproject> list() {
    Query query = currentSession().createQuery("from SubprojectImpl");
    @SuppressWarnings("unchecked")
    List<Subproject> records = query.list();
    return records;
  }

  @Override
  public Subproject get(long id) {
    return (Subproject) currentSession().get(SubprojectImpl.class, id);
  }

  @Override
  public long create(Subproject subproject) {
    Date now = new Date();
    subproject.setCreationTime(now);
    subproject.setLastModified(now);
    return (Long) currentSession().save(subproject);
  }

  @Override
  public void delete(Subproject subproject) {
    currentSession().delete(subproject);

  }

  @Override
  public long update(Subproject subproject) {
    Date now = new Date();
    subproject.setLastModified(now);
    currentSession().update(subproject);
    return subproject.getId();
  }

  @Override
  public long getUsage(Subproject subproject) {
    return (long) currentSession().createCriteria(DetailedSampleImpl.class)
        .add(Restrictions.eq("subproject", subproject))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

  @Override
  public List<Subproject> listByProjectId(Long projectId) {
    @SuppressWarnings("unchecked")
    List<Subproject> subprojects = currentSession().createCriteria(SubprojectImpl.class)
        .add(Restrictions.eq("parentProject.id", projectId))
        .list();
    return subprojects;
  }

  @Override
  public Subproject getByAlias(String alias) {
    return (Subproject) currentSession().createCriteria(SubprojectImpl.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

}
