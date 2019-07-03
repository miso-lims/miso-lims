package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.persistence.ReferenceGenomeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateReferenceGenomeDao implements ReferenceGenomeDao {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<ReferenceGenome> list() {
    Query query = currentSession().createQuery("from ReferenceGenomeImpl");
    @SuppressWarnings("unchecked")
    List<ReferenceGenome> records = query.list();
    return records;
  }

  @Override
  public ReferenceGenome get(long id) {
    return (ReferenceGenome) currentSession().get(ReferenceGenomeImpl.class, id);
  }

  @Override
  public ReferenceGenome getByAlias(String alias) {
    return (ReferenceGenome) currentSession().createCriteria(ReferenceGenomeImpl.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public long create(ReferenceGenome reference) {
    return (long) currentSession().save(reference);
  }

  @Override
  public long update(ReferenceGenome reference) {
    currentSession().update(reference);
    return reference.getId();
  }

  @Override
  public long getUsage(ReferenceGenome reference) {
    return (long) currentSession().createCriteria(ProjectImpl.class)
        .add(Restrictions.eq("referenceGenome", reference))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
