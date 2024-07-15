package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily_;
import uk.ac.bbsrc.tgac.miso.core.data.Index_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.IndexStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateIndexDao extends HibernateSaveDao<Index>
    implements IndexStore, JpaCriteriaPaginatedDataSource<Index, Index> {

  private static final List<SingularAttribute<? super Index, String>> SEARCH_PROPERTIES =
      Arrays.asList(Index_.name, Index_.sequence);

  public HibernateIndexDao() {
    super(Index.class);
  }

  @Override
  public String getFriendlyName() {
    return "Index";
  }

  @Override
  public SingularAttribute<Index, ?> getIdProperty() {
    return Index_.indexId;
  }

  @Override
  public Class<Index> getEntityClass() {
    return Index.class;
  }

  @Override
  public Class<Index> getResultClass() {
    return Index.class;
  }

  @Override
  public List<SingularAttribute<? super Index, String>> getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public SingularAttribute<Index, ?> propertyForDate(DateType type) {
    return null;
  }

  @Override
  public Path<?> propertyForSortColumn(Root<Index> root, String original) {
    Path<?> family = root.get(Index_.family);
    if ("family.platformType".equals(original))
      return family.get(IndexFamily_.PLATFORM_TYPE);
    if ("family.name".equals(original))
      return family.get(IndexFamily_.NAME);
    return root.get(original);
  }

  @Override
  public SingularAttribute<Index, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }



  @Override
  public void restrictPaginationByArchived(QueryBuilder<?, Index> builder, boolean isArchived,
      Consumer<String> errorHandler) {
    Join<Index, IndexFamily> indexJoin = builder.getJoin(builder.getRoot(), Index_.family);
    builder.addPredicate(builder.getCriteriaBuilder().equal(indexJoin.get(IndexFamily_.archived), isArchived));
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, Index> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    Join<Index, IndexFamily> indexJoin = builder.getJoin(builder.getRoot(), Index_.family);
    builder.addPredicate(builder.getCriteriaBuilder().equal(indexJoin.get(IndexFamily_.platformType), platformType));
  }

  @Override
  public Index getByFamilyPositionAndName(IndexFamily family, int position, String name) throws IOException {
    QueryBuilder<Index, Index> builder = new QueryBuilder<>(currentSession(), Index.class, Index.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Index_.family), family));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Index_.position), position));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Index_.name), name));
    return builder.getSingleResultOrNull();
  }

  @Override
  public long getUsage(Index index) throws IOException {
    LongQueryBuilder<LibraryImpl> builder = new LongQueryBuilder<>(currentSession(), LibraryImpl.class);
    Join<LibraryImpl, Index> index1Join = builder.getJoin(builder.getRoot(), LibraryImpl_.index1);
    Join<LibraryImpl, Index> index2Join = builder.getJoin(builder.getRoot(), LibraryImpl_.index2);
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().equal(index1Join.get(Index_.indexId), index.getId()),
        builder.getCriteriaBuilder().equal(index2Join.get(Index_.indexId), index.getId())));
    return builder.getCount();
  }

  @Override
  public List<Index> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(Index_.INDEX_ID, ids);
  }

}
