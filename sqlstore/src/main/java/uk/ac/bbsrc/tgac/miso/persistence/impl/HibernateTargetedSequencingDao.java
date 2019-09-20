package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.TargetedSequencingStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTargetedSequencingDao implements TargetedSequencingStore, HibernatePaginatedDataSource<TargetedSequencing> {

  protected static final Logger log = LoggerFactory.getLogger(HibernateTargetedSequencingDao.class);

  private static final String[] SEARCH_PROPERTIES = new String[] { "alias" };

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public TargetedSequencing get(long id) throws IOException {
    return (TargetedSequencing) currentSession().get(TargetedSequencing.class, id);
  }

  @Override
  public List<TargetedSequencing> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(TargetedSequencing.class);
    @SuppressWarnings("unchecked")
    List<TargetedSequencing> records = criteria.list();
    return records;
  }

  @Override
  public List<TargetedSequencing> list(List<Long> targetedSequencingIds) throws IOException {
    if (targetedSequencingIds.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(TargetedSequencing.class);
    criteria.add(Restrictions.in("id", targetedSequencingIds));
    @SuppressWarnings("unchecked")
    List<TargetedSequencing> results = criteria.list();
    return results;
  }


  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(TargetedSequencing.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public long save(TargetedSequencing ts) throws IOException {
    if (!ts.isSaved()) {
      throw new UnsupportedOperationException("Create not supported for targeted sequencing");
    } else {
      currentSession().update(ts);
      return ts.getId();
    }
  }

  @Override
  public String getFriendlyName() {
    return "Targeted Sequencing";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends TargetedSequencing> getRealClass() {
    return TargetedSequencing.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<String> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    return null;
  }

  @Override
  public String propertyForId() {
    return "targetedSequencingId";
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForUser(boolean creator) {
    return null;
  }

}
