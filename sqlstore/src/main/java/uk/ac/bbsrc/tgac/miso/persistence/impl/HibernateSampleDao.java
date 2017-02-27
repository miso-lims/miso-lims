package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * This is the Hibernate DAO for Samples and serves as the bridge between Hibernate and the existing SqlStore persistence layers.
 *
 * The data from the Sample table is loaded via Hibernate, but Hibernate cannot follow the references to Libraries and such from a Sample.
 * Therefore, this implementation loads a Sample via Hibernate, then calls into the SqlStore persistence layer to gather the remaining data
 * that Hibernate cannot access. Similarly, it then follows any necessary links on save. All the SqlStore-populated fields are marked
 * “transient” in the Sample class.
 */
@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleDao implements SampleDao, SiblingNumberGenerator {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate template;

  @Override
  public Long addSample(final Sample sample) throws IOException {
    return (Long) currentSession().save(sample);
  }

  @Override
  public int getNextSiblingNumber(String partialAlias) throws IOException {
    // Find highest existing siblingNumber matching this partialAlias
    Query query = currentSession().createQuery("select max(siblingNumber) from DetailedSampleImpl as ds"
        + " where alias IN (concat(:alias, ds.siblingNumber), concat(:alias, '0', ds.siblingNumber))");
    query.setString("alias", partialAlias);
    Number result = ((Number) query.uniqueResult());
    int next = result == null ? 0 : result.intValue();

    // Increment and verify uniqueness. If alias is used, fix siblingNumber for existing sample. Repeat until unique
    Query verifyQuery = null;
    do {
      next++;
      verifyQuery = currentSession().createQuery("update DetailedSampleImpl ds set ds.siblingNumber = :siblingNumber"
          + " where alias IN (concat(:alias, :siblingNumber), concat(:alias, '0', :siblingNumber))");
      verifyQuery.setString("alias", partialAlias).setString("siblingNumber", String.valueOf(next));
    } while (verifyQuery.executeUpdate() > 0);

    return next;
  }

  @Override
  public int count() throws IOException {
    return getSamples().size();
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public void deleteSample(Sample sample) {
    currentSession().delete(sample);
  }

  @Override
  public Sample get(long id) throws IOException {
    return getSample(id);
  }

  @Override
  public Sample getByBarcode(String barcode) throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (Sample) criteria.uniqueResult();
  }

  @Override
  public Collection<Sample> getByBarcodeList(List<String> barcodeList) throws IOException {
    if (barcodeList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.in("identificationBarcode", barcodeList));
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Sample> getByIdList(List<Long> idList) throws IOException {
    if (idList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.in("sampleId", idList));
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  @Override
  public List<Sample> getSamples() throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  @Override
  public Sample getSample(Long id) throws IOException {
    return (Sample) currentSession().get(SampleImpl.class, id);
  }

  @Override
  public Long countAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public Collection<Sample> listAll() throws IOException {
    return getSamples();
  }

  @Override
  public Collection<Sample> listAllByReceivedDate(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.addOrder(Order.desc("receivedDate"));
    criteria.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  @Override
  public Collection<String> listAllSampleTypes() throws IOException {
    return getJdbcTemplate().queryForList("SELECT name FROM SampleType", String.class);
  }

  @Override
  public Collection<Sample> listAllWithLimit(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  @Override
  public boolean aliasExists(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult() > 0;
  }

  @Override
  public Collection<Sample> listByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  /**
   * Lazy-gets samples associated with a given Project
   *
   * @param Long
   *          projectId
   * @return Collection<Sample> samples
   */
  @Override
  public Collection<Sample> listByProjectId(long projectId) throws IOException {
    @SuppressWarnings("unchecked")
    List<Sample> records = currentSession().createCriteria(SampleImpl.class).add(Restrictions.eq("project.id", projectId)).list();
    return records;
  }

  /**
   * Create a Hibernate criterion to search for all the properties our users want to search.
   *
   * @param querystr
   * @return
   */
  private Criterion searchRestrictions(String querystr) {
    Criterion search = DbUtils.searchRestrictions(querystr, "alias", "identificationBarcode", "name");

    String str = DbUtils.convertStringToSearchQuery(querystr);
    Criterion classCheck = Restrictions.and(Restrictions.eq("class", IdentityImpl.class),
        Restrictions.ilike("externalName", str, MatchMode.ANYWHERE));
    return Restrictions.or(search, classCheck);
  }

  @Override
  public Collection<Sample> listBySearch(String querystr) throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(searchRestrictions(querystr));
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  @Override
  public Long countBySearch(String querystr) throws IOException {
    if (isStringEmptyOrNull(querystr)) {
      return countAll();
    } else {
      Criteria criteria = currentSession().createCriteria(SampleImpl.class);
      criteria.add(searchRestrictions(querystr));
      return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }
  }

  @Override
  public List<Sample> listBySearchOffsetAndNumResults(int offset, int resultsPerPage, String querystr, String sortCol, String sortDir)
      throws IOException {
    if (offset < 0 || resultsPerPage < 0) throw new IOException("Limit and Offset must not be less than zero");
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(searchRestrictions(querystr));
    // required to sort by 'derivedInfo.lastModified', which is the field on which we
    // want to sort most List X pages
    criteria.createAlias("derivedInfo", "derivedInfo");
    criteria.setFirstResult(offset);
    criteria.setMaxResults(resultsPerPage);
    criteria.addOrder("asc".equalsIgnoreCase(sortDir) ? Order.asc(sortCol) : Order.desc(sortCol));
    criteria.setProjection(Projections.property("id"));
    @SuppressWarnings("unchecked")
    List<Long> ids = criteria.list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    // We do this in two steps to make a smaller query that that the database can optimise
    Criteria sampleCriteria = currentSession().createCriteria(SampleImpl.class);
    sampleCriteria.add(Restrictions.in("id", ids));
    sampleCriteria.addOrder("asc".equalsIgnoreCase(sortDir) ? Order.asc(sortCol) : Order.desc(sortCol));
    sampleCriteria.createAlias("derivedInfo", "derivedInfo");
    @SuppressWarnings("unchecked")
    List<Sample> requestedPage = sampleCriteria.list();
    return requestedPage;
  }

  @Override
  public List<Sample> listByOffsetAndNumResults(int offset, int resultsPerPage, String sortCol, String sortDir) throws IOException {
    if (offset < 0 || resultsPerPage < 0) throw new IOException("Limit and Offset must not be less than zero");
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    // required to sort by 'derivedInfo.lastModified', which is the field on which we
    // want to sort most List X pages
    criteria.createAlias("derivedInfo", "derivedInfo");
    criteria.setFirstResult(offset);
    criteria.setMaxResults(resultsPerPage);
    criteria.addOrder("asc".equalsIgnoreCase(sortDir.toLowerCase()) ? Order.asc(sortCol) : Order.desc(sortCol));
    @SuppressWarnings("unchecked")
    List<Sample> requestedPage = criteria.list();
    return requestedPage;
  }

  @Override
  public boolean remove(Sample t) throws IOException {
    deleteSample(t);
    return true;
  }

  @Override
  public long save(Sample t) throws IOException {
    if (t.getId() == SampleImpl.UNSAVED_ID) {
      return addSample(t);
    } else {
      update(t);
      return t.getId();
    }
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public void update(Sample sample) throws IOException {
    currentSession().update(sample);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Map<String, Integer> getSampleColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, "Sample");
  }

  @Override
  public Collection<Identity> getIdentitiesByExternalNameOrAlias(String externalName) throws IOException {
    if (isStringEmptyOrNull(externalName)) return Collections.emptySet();
    String str = DbUtils.convertStringToSearchQuery(externalName);
    Criteria criteria = currentSession().createCriteria(IdentityImpl.class);
    criteria.add(Restrictions.or(Restrictions.ilike("externalName", str), Restrictions.ilike("alias", str)));
    @SuppressWarnings("unchecked")
    Collection<Identity> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Identity> getIdentitiesByExternalNameAndProject(String externalName, Long projectId) throws IOException {
    if (isStringEmptyOrNull(externalName)) return Collections.emptySet();
    if (projectId == null) throw new IllegalArgumentException("Must provide a projectId in search");
    String str = DbUtils.convertStringToSearchQuery(externalName);
    Criteria criteria = currentSession().createCriteria(IdentityImpl.class);
    criteria.add(Restrictions.eq("project.id", projectId));
    criteria.add(Restrictions.or(Restrictions.ilike("externalName", str), Restrictions.ilike("alias", str)));
    @SuppressWarnings("unchecked")
    Collection<Identity> records = criteria.list();
    return records;
  }

  @Override
  public Sample getByPreMigrationId(Long id) throws IOException {
    Criteria criteria = currentSession().createCriteria(DetailedSampleImpl.class);
    criteria.add(Restrictions.eq("preMigrationId", id));
    return (Sample) criteria.uniqueResult();
  }
}
