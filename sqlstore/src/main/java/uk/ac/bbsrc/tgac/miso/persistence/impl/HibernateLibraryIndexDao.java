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
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily_;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryIndexDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateLibraryIndexDao extends HibernateSaveDao<LibraryIndex>
    implements LibraryIndexDao, JpaCriteriaPaginatedDataSource<LibraryIndex, LibraryIndex> {

  public HibernateLibraryIndexDao() {
    super(LibraryIndex.class);
  }

  @Override
  public String getFriendlyName() {
    return "Index";
  }

  @Override
  public SingularAttribute<LibraryIndex, ?> getIdProperty() {
    return LibraryIndex_.indexId;
  }

  @Override
  public Class<LibraryIndex> getEntityClass() {
    return LibraryIndex.class;
  }

  @Override
  public Class<LibraryIndex> getResultClass() {
    return LibraryIndex.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<LibraryIndex> root) {
    return Arrays.asList(root.get(LibraryIndex_.name), root.get(LibraryIndex_.sequence));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, LibraryIndex> builder, DateType type) {
    return null;
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, LibraryIndex> builder, String original) {
    Join<LibraryIndex, LibraryIndexFamily> family = builder.getJoin(builder.getRoot(), LibraryIndex_.family);
    switch (original) {
      case "family.platformType":
        return family.get(LibraryIndexFamily_.platformType);
      case "family.name":
        return family.get(LibraryIndexFamily_.name);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<LibraryIndex, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByArchived(QueryBuilder<?, LibraryIndex> builder, boolean isArchived,
      Consumer<String> errorHandler) {
    Join<LibraryIndex, LibraryIndexFamily> indexJoin = builder.getJoin(builder.getRoot(), LibraryIndex_.family);
    builder.addPredicate(builder.getCriteriaBuilder().equal(indexJoin.get(LibraryIndexFamily_.archived), isArchived));
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, LibraryIndex> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    Join<LibraryIndex, LibraryIndexFamily> indexJoin = builder.getJoin(builder.getRoot(), LibraryIndex_.family);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(indexJoin.get(LibraryIndexFamily_.platformType), platformType));
  }

  @Override
  public LibraryIndex getByFamilyPositionAndName(LibraryIndexFamily family, int position, String name)
      throws IOException {
    QueryBuilder<LibraryIndex, LibraryIndex> builder =
        new QueryBuilder<>(currentSession(), LibraryIndex.class, LibraryIndex.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryIndex_.family), family));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryIndex_.position), position));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryIndex_.name), name));
    return builder.getSingleResultOrNull();
  }

  @Override
  public long getUsage(LibraryIndex index) throws IOException {
    LongQueryBuilder<LibraryImpl> builder = new LongQueryBuilder<>(currentSession(), LibraryImpl.class);
    Join<LibraryImpl, LibraryIndex> index1Join = builder.getJoin(builder.getRoot(), LibraryImpl_.index1);
    Join<LibraryImpl, LibraryIndex> index2Join = builder.getJoin(builder.getRoot(), LibraryImpl_.index2);
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().equal(index1Join.get(LibraryIndex_.indexId), index.getId()),
        builder.getCriteriaBuilder().equal(index2Join.get(LibraryIndex_.indexId), index.getId())));
    return builder.getCount();
  }

  @Override
  public List<LibraryIndex> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(LibraryIndex_.INDEX_ID, ids);
  }

}
