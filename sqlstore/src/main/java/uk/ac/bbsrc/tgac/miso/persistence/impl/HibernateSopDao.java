package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.SopDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSopDao extends HibernateSaveDao<Sop> implements JpaCriteriaPaginatedDataSource<Sop, Sop>, SopDao {

  public HibernateSopDao() {
    super(Sop.class);
  }

  @Override
  public Sop get(SopCategory category, String alias, String version) throws IOException {
    QueryBuilder<Sop, Sop> builder = getQueryBuilder();
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Sop_.category), category));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Sop_.alias), alias));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Sop_.version), version));
    return builder.getSingleResultOrNull();
  }

  @Override
  public List<Sop> listByCategory(SopCategory category) throws IOException {
    QueryBuilder<Sop, Sop> builder = getQueryBuilder();
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Sop_.category), category));
    return builder.getResultList();
  }

  @Override
  public List<Sop> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(Sop_.SOP_ID, ids);
  }

  @Override
  public long getUsageBySamples(Sop sop) throws IOException {
    return getUsageBy(SampleImpl.class, SampleImpl_.sop, sop);
  }

  @Override
  public long getUsageByLibraries(Sop sop) throws IOException {
    return getUsageBy(LibraryImpl.class, LibraryImpl_.sop, sop);
  }

  @Override
  public long getUsageByRuns(Sop sop) throws IOException {
    return getUsageBy(Run.class, Run_.sop, sop);
  }

  @Override
  public String getFriendlyName() {
    return "SOP";
  }

  @Override
  public SingularAttribute<Sop, ?> getIdProperty() {
    return Sop_.sopId;
  }

  @Override
  public Class<Sop> getEntityClass() {
    return Sop.class;
  }

  @Override
  public Class<Sop> getResultClass() {
    return Sop.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<Sop> root) {
    return Arrays.asList(root.get(Sop_.alias));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, Sop> builder, DateType type) {
    return null;
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, Sop> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(Sop_.sopId);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<Sop, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByCategory(QueryBuilder<?, Sop> builder, SopCategory category,
      Consumer<String> errorHandler) {
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Sop_.category), category));
  }

}
