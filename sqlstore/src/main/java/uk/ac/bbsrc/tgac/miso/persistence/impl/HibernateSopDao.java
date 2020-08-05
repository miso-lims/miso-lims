package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.SopDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSopDao extends HibernateSaveDao<Sop> implements HibernatePaginatedDataSource<Sop>, SopDao {

  private final static String[] SEARCH_PROPERTIES = new String[] { "alias" };

  public HibernateSopDao() {
    super(Sop.class);
  }

  @Override
  public Sop get(SopCategory category, String alias, String version) throws IOException {
    return (Sop) currentSession().createCriteria(Sop.class)
        .add(Restrictions.eq("category", category))
        .add(Restrictions.eq("alias", alias))
        .add(Restrictions.eq("version", version))
        .uniqueResult();
  }

  @Override
  public List<Sop> listByCategory(SopCategory category) throws IOException {
    @SuppressWarnings("unchecked")
    List<Sop> results = currentSession().createCriteria(Sop.class)
        .add(Restrictions.eq("category", category))
        .list();
    return results;
  }

  @Override
  public List<Sop> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("sopId", ids);
  }

  @Override
  public long getUsageBySamples(Sop sop) throws IOException {
    return getUsageBy(SampleImpl.class, "sop", sop);
  }

  @Override
  public long getUsageByLibraries(Sop sop) throws IOException {
    return getUsageBy(LibraryImpl.class, "sop", sop);
  }

  @Override
  public long getUsageByRuns(Sop sop) throws IOException {
    return getUsageBy(Run.class, "sop", sop);
  }

  @Override
  public String getFriendlyName() {
    return "SOP";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends Sop> getRealClass() {
    return Sop.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptySet();
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
  public void restrictPaginationByCategory(Criteria item, SopCategory category, Consumer<String> errorHandler) {
    item.add(Restrictions.eq("category", category));
  }

}
