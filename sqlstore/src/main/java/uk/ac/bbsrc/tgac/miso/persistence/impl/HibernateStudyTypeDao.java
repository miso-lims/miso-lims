package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.persistence.StudyTypeDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStudyTypeDao implements StudyTypeDao {

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public StudyType get(long id) throws IOException {
    return (StudyType) currentSession().get(StudyType.class, id);
  }

  @Override
  public StudyType getByName(String name) throws IOException {
    return (StudyType) currentSession().createCriteria(StudyType.class)
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public List<StudyType> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<StudyType> results = currentSession().createCriteria(StudyType.class).list();
    return results;
  }

  @Override
  public long create(StudyType type) throws IOException {
    return (long) currentSession().save(type);
  }

  @Override
  public long update(StudyType type) throws IOException {
    currentSession().update(type);
    return type.getId();
  }

  @Override
  public long getUsage(StudyType type) throws IOException {
    return (long) currentSession().createCriteria(StudyImpl.class)
        .add(Restrictions.eq("studyType", type))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
