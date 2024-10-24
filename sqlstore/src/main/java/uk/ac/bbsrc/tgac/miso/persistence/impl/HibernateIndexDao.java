package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
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
  public List<Path<String>> getSearchProperties(Root<Index> root) {
    return Arrays.asList(root.get(Index_.name), root.get(Index_.sequence));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, Index> builder, DateType type) {
    return null;
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, Index> builder, String original) {
    Join<Index, IndexFamily> family = builder.getJoin(builder.getRoot(), Index_.family);
    switch (original) {
      case "family.platformType":
        return family.get(IndexFamily_.platformType);
      case "family.name":
        return family.get(IndexFamily_.name);
      default:
        return builder.getRoot().get(original);
    }
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
