package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.IndexStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateIndexDao extends HibernateSaveDao<Index> implements IndexStore, HibernatePaginatedDataSource<Index> {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSubprojectDao.class);

  private static final String[] SEARCH_PROPERTIES = new String[] { "name", "sequence", "family.name" };

  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("family"));

  public HibernateIndexDao() {
    super(Index.class);
  }

  @Override
  public Session currentSession() {
    return super.currentSession();
  }

  @Override
  public String getFriendlyName() {
    return "Index";
  }

  @Override
  public Index get(long id) {
    Query query = currentSession().createQuery("from Index where id = :id");
    query.setLong("id", id);
    return (Index) query.uniqueResult();
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends Index> getRealClass() {
    return Index.class;
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
    return null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByArchived(Criteria criteria, boolean isArchived, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("family.archived", isArchived));
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("family.platformType", platformType));
  }

  @Override
  public Index getByFamilyPositionAndName(IndexFamily family, int position, String name) throws IOException {
    return (Index) currentSession().createCriteria(Index.class)
        .add(Restrictions.eq("family", family))
        .add(Restrictions.eq("position", position))
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public long getUsage(Index index) throws IOException {
    return (long) currentSession().createCriteria(LibraryImpl.class)
        .createAlias("indices", "index")
        .add(Restrictions.eq("index.id", index.getId()))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

}
