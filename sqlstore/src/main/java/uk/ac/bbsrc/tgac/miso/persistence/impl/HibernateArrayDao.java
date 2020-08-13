package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateArrayDao implements ArrayStore, HibernatePaginatedDataSource<Array> {

  private static final String FIELD_ALIAS = "alias";
  private static final String FIELD_SERIALNUM = "serialNumber";

  private static final String[] SEARCH_PROPERTIES = new String[] { FIELD_ALIAS, FIELD_SERIALNUM, "description" };
  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("arrayModel"));

  @Autowired
  private SessionFactory sessionFactory;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void setDetailedSample(boolean isDetailed) {
    this.detailedSample = isDetailed;
  }

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(Array array) throws IOException {
    if (!array.isSaved()) {
      return (long) currentSession().save(array);
    } else {
      currentSession().update(array);
      return array.getId();
    }
  }

  @Override
  public Array get(long id) throws IOException {
    return (Array) currentSession().get(Array.class, id);
  }

  @Override
  public Array getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(Array.class);
    criteria.add(Restrictions.eq(FIELD_ALIAS, alias));
    return (Array) criteria.uniqueResult();
  }

  @Override
  public Array getBySerialNumber(String serialNumber) throws IOException {
    Criteria criteria = currentSession().createCriteria(Array.class);
    criteria.add(Restrictions.eq(FIELD_SERIALNUM, serialNumber));
    return (Array) criteria.uniqueResult();
  }

  @Override
  public List<Array> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(Array.class);
    @SuppressWarnings("unchecked")
    List<Array> list = criteria.list();
    return list;
  }

  @Override
  public List<Array> listBySampleId(long sampleId) throws IOException {
    Criteria criteria = currentSession().createCriteria(Array.class);
    criteria.createAlias("samples", "sample");
    criteria.add(Restrictions.eqOrIsNull("sample.id", sampleId));
    @SuppressWarnings("unchecked")
    List<Array> list = criteria.list();
    return list;
  }

  @Override
  public List<Sample> getArrayableSamplesBySearch(String search) throws IOException {
    if (search == null) {
      throw new NullPointerException("No search String provided");
    }
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.or(
        Restrictions.eq("identificationBarcode", search),
        Restrictions.eq("name", search),
        Restrictions.eq(FIELD_ALIAS, search)
        ));
    if (detailedSample) {
      criteria.createAlias("sampleClass", "sampleClass");
      criteria.add(Restrictions.eq("sampleClass.sampleCategory", SampleAliquot.CATEGORY_NAME));
    }
    criteria.add(Restrictions.eq("discarded", false));
    @SuppressWarnings("unchecked")
    List<Sample> results = criteria.list();
    return results;
  }

  @Override
  public List<Array> getArraysBySearch(String search) throws IOException {
    Criteria criteria = currentSession().createCriteria(Array.class);
    criteria.add(Restrictions.or(
        Restrictions.eq(FIELD_SERIALNUM, search),
        Restrictions.eq(FIELD_ALIAS, search)
        ));
    @SuppressWarnings("unchecked")
    List<Array> results = criteria.list();
    return results;
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(Array.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public String getFriendlyName() {
    return "Array";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends Array> getRealClass() {
    return Array.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case ENTERED:
      return "creationTime";
    case UPDATE:
      return "lastModified";
    default:
      return null;
    }
  }

  @Override
  public String propertyForSortColumn(String original) {
    if ("arrayModelId".equals(original)) {
      return "arrayModel.id";
    } else {
      return original;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

}
