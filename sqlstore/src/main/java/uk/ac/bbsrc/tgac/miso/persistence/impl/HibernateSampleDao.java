package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
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

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
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
public class HibernateSampleDao implements SampleDao, SiblingNumberGenerator, HibernatePaginatedBoxableSource<Sample> {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleDao.class);

  private final static String[] SEARCH_PROPERTIES = new String[] { "alias", "identificationBarcode", "name" };

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private BoxStore boxStore;

  @Autowired
  private JdbcTemplate template;

  public void setBoxStore(BoxStore boxStore) {
    this.boxStore = boxStore;
  }

  @Override
  public Long addSample(final Sample sample) throws IOException {
    return (Long) currentSession().save(sample);
  }

  @Override
  public int getNextSiblingNumber(String partialAlias) throws IOException {
    // Find highest existing siblingNumber matching this partialAlias
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.like("alias", partialAlias, MatchMode.START));
    @SuppressWarnings("unchecked")
    List<Sample> samples = criteria.list();
    String regex = "^.{" + partialAlias.length() + "}(\\d*)$";
    Pattern pattern = Pattern.compile(regex);
    int next = 0;
    for (Sample sample : samples) {
      Matcher m = pattern.matcher(sample.getAlias());
      if (!m.matches()) {
        continue;
      }
      int siblingNumber = Integer.parseInt(m.group(1));
      if (siblingNumber > next) {
        next = siblingNumber;
      }
    }
    next++;
    return next;
  }

  @Override
  public int count() throws IOException {
    return list().size();
  }

  @Override
  public Session currentSession() {
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
  public Collection<Sample> getByBarcodeList(Collection<String> barcodeList) throws IOException {
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
  public List<Sample> list() throws IOException {
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
    return list();
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

  @Override
  public void restrictPaginationByExternalName(Criteria criteria, String name, Consumer<String> errorHandler) {
    // TODO: this should extends to the children of the entity with this external name (including libraries and dilutions)
    String query = DbUtils.convertStringToSearchQuery(name);
    Disjunction or = Restrictions.disjunction();
    or.add(externalNameCheck(SampleIdentityImpl.class, "externalName", query));
    or.add(externalNameCheck(SampleTissueImpl.class, "externalInstituteIdentifier", query));
    criteria.add(or);
  }

  private Criterion externalNameCheck(Class<? extends DetailedSample> clazz, String property, String query) {
    return Restrictions.and(Restrictions.eq("class", clazz),
        Restrictions.ilike(property, query, MatchMode.ANYWHERE));
  }

  @Override
  public void restrictPaginationByInstitute(Criteria criteria, String name, Consumer<String> errorHandler) {
    // TODO: this should extends to the children of the entity with this lab (including libraries and dilutions)
    criteria.createAlias("lab", "lab");
    criteria.createAlias("lab.institute", "institute");
    criteria.add(DbUtils.searchRestrictions(name, "lab.alias", "institute.alias"));
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
    if (sample.isDiscarded()) {
      boxStore.removeBoxableFromBox(sample);
    }
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
  public Collection<SampleIdentity> getIdentitiesByExternalNameOrAlias(String externalName) throws IOException {
    if (isStringEmptyOrNull(externalName)) return Collections.emptySet();
    String str = DbUtils.convertStringToSearchQuery(externalName);
    Criteria criteria = currentSession().createCriteria(SampleIdentityImpl.class);
    criteria.add(Restrictions.or(Restrictions.ilike("externalName", str), Restrictions.ilike("alias", str)));
    @SuppressWarnings("unchecked")
    Collection<SampleIdentity> records = criteria.list();
    return records;
  }

  @Override
  public Collection<SampleIdentity> getIdentitiesByExternalNameAndProject(String externalName, Long projectId) throws IOException {
    if (isStringEmptyOrNull(externalName)) return Collections.emptySet();
    if (projectId == null) throw new IllegalArgumentException("Must provide a projectId in search");
    Criteria criteria = currentSession().createCriteria(SampleIdentityImpl.class);
    criteria.add(Restrictions.eq("project.id", projectId));
    criteria.add(Restrictions.eq("externalName", externalName).ignoreCase());
    @SuppressWarnings("unchecked")
    Collection<SampleIdentity> records = criteria.list();
    return records;
  }

  @Override
  public Sample getByPreMigrationId(Long id) throws IOException {
    Criteria criteria = currentSession().createCriteria(DetailedSampleImpl.class);
    criteria.add(Restrictions.eq("preMigrationId", id));
    return (Sample) criteria.uniqueResult();
  }

  private static final List<String> STANDARD_ALIASES = Arrays.asList("lastModifier", "creator");

  @Override
  public String getProjectColumn() {
    return "project.id";
  }

  @Override
  public Iterable<String> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public Class<? extends Sample> getRealClass() {
    return SampleImpl.class;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case CREATE:
      return "creationTime";
    case UPDATE:
      return "lastModified";
    case RECEIVE:
      return "receivedDate";
    default:
      return null;
    }
  }

  @Override
  public String propertyForUserName(Criteria criteria, boolean creator) {
    return creator ? "creator.loginName" : "lastModifier.loginName";
  }

  @Override
  public void restrictPaginationByClass(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.createAlias("sampleClass", "sampleClass");
    criteria.add(Restrictions.or(Restrictions.ilike("sampleClass.alias", name, MatchMode.ANYWHERE),
        Restrictions.ilike("sampleClass.sampleCategory", name, MatchMode.START)));
  }

  @Override
  public String getFriendlyName() {
    return "Sample";
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

}
