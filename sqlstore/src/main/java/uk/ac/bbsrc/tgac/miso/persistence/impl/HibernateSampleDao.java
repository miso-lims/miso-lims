package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleDao implements SampleStore, HibernatePaginatedBoxableSource<Sample> {

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
  public Sample getSample(long id) throws IOException {
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
  public Collection<String> listSampleTypes() throws IOException {
    return getJdbcTemplate().queryForList("SELECT name FROM SampleType WHERE archived = 0", String.class);
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
    String query = DbUtils.convertStringToSearchQuery(name, false);
    Disjunction or = Restrictions.disjunction();
    or.add(externalNameCheck(SampleIdentityImpl.class, "externalName", query));
    or.add(externalNameCheck(SampleTissueImpl.class, "secondaryIdentifier", query));
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
    criteria.add(DbUtils.searchRestrictions(name, false, "lab.alias", "institute.alias"));
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
  public Collection<SampleIdentity> getIdentitiesByExternalNameOrAliasAndProject(String query, Long projectId, boolean exactMatch)
      throws IOException {
    if (isStringEmptyOrNull(query)) return Collections.emptySet();
    @SuppressWarnings("unchecked")
    Set<SampleIdentity> records = (Set<SampleIdentity>) SampleIdentityImpl.getSetFromString(query)
        .stream().map(extNameOrAlias -> {
          String str = DbUtils.convertStringToSearchQuery(extNameOrAlias, false);
          Criteria criteria = currentSession().createCriteria(SampleIdentityImpl.class);
          if (projectId != null) {
            criteria.add(Restrictions.eq("project.id", projectId));
          }
          criteria.add(Restrictions.or(Restrictions.ilike("externalName", str), Restrictions.ilike("alias", str)));
          return criteria.list();
        }).flatMap(list -> list.stream())
        .distinct()
        .collect(Collectors.toSet());
    
    // filter out those with a non-exact external name match
    if (exactMatch) {
      return filterOnlyExactExternalNameMatches(records, query);
    } else {
      return records;
    }
  }

  private Collection<SampleIdentity> filterOnlyExactExternalNameMatches(Collection<SampleIdentity> candidates,
      String externalNamesOrAlias) {
    return candidates.stream().filter(sam -> {
      Set<String> targets = SampleIdentityImpl.getSetFromString(externalNamesOrAlias).stream().map(String::toLowerCase)
          .collect(Collectors.toSet());
      Set<String> externalNamesOfCandidate = SampleIdentityImpl.getSetFromString(sam.getExternalName()).stream()
          .map(String::toLowerCase).collect(Collectors.toSet());
      targets.retainAll(externalNamesOfCandidate);
      return !targets.isEmpty() || externalNamesOrAlias.equals(sam.getAlias());
    }).collect(Collectors.toSet());
  }

  @Override
  public SampleTissue getMatchingGhostTissue(SampleTissue tissue) {
    validateGhostTissueLookup(tissue);
    Criteria criteria = currentSession().createCriteria(SampleTissueImpl.class);
    criteria.add(Restrictions.eq("isSynthetic", true));
    criteria.add(Restrictions.eq("parent.id", tissue.getParent().getId()));
    criteria.add(Restrictions.eq("tissueOrigin.id", tissue.getTissueOrigin().getId()));
    criteria.add(Restrictions.eq("tissueType.id", tissue.getTissueType().getId()));
    criteria.add(Restrictions.eq("timesReceived", tissue.getTimesReceived()));
    criteria.add(Restrictions.eq("tubeNumber", tissue.getTubeNumber()));
    if (tissue.getPassageNumber() == null) {
      criteria.add(Restrictions.isNull("passageNumber"));
    } else {
      criteria.add(Restrictions.eq("passageNumber", tissue.getPassageNumber()));
    }
    return (SampleTissue) criteria.uniqueResult();
  }

  private void validateGhostTissueLookup(SampleTissue tissue) {
    if (tissue.getParent() == null
        || tissue.getParent().getId() == SampleImpl.UNSAVED_ID
        || tissue.getTissueOrigin() == null
        || tissue.getTissueOrigin().getId() == TissueOrigin.UNSAVED_ID
        || tissue.getTissueType() == null
        || tissue.getTissueType().getId() == TissueTypeImpl.UNSAVED_ID
        || tissue.getTimesReceived() == null
        || tissue.getTubeNumber() == null) {
      throw new IllegalArgumentException("Missing tissue attributes required for lookup");
    }
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
  public void restrictPaginationByArrayed(Criteria criteria, boolean isArrayed, Consumer<String> errorHandler) {
    DetachedCriteria subquery = DetachedCriteria.forClass(Array.class)
        .createAlias("samples", "sample")
        .setProjection(Projections.property("sample.id"));
    if (isArrayed) {
      criteria.add(Property.forName("id").in(subquery));
    } else {
      criteria.add(Property.forName("id").notIn(subquery));
    }
  }

  @Override
  public void restrictPaginationBySubproject(Criteria criteria, String subproject, Consumer<String> errorHandler) {
    criteria.createAlias("subproject", "subproject");
    criteria.add(Restrictions.ilike("subproject.alias", subproject, MatchMode.START));
  }

  @Override
  public String getFriendlyName() {
    return "Sample";
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public long getChildSampleCount(Sample sample) {
    return (long) currentSession().createCriteria(DetailedSampleImpl.class)
        .add(Restrictions.eqOrIsNull("parent", sample))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

}
