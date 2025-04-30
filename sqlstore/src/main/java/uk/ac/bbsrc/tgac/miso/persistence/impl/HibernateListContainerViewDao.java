package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListContainerView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListContainerView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement_;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ListContainerViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListContainerViewDao
    implements ListContainerViewDao, JpaCriteriaPaginatedDataSource<ListContainerView, ListContainerView> {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public String getFriendlyName() {
    return "Container";
  }

  @Override
  public SingularAttribute<ListContainerView, ?> getIdProperty() {
    return ListContainerView_.containerId;
  }

  @Override
  public Class<ListContainerView> getEntityClass() {
    return ListContainerView.class;
  }

  @Override
  public Class<ListContainerView> getResultClass() {
    return ListContainerView.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<ListContainerView> root) {
    return Arrays.asList(root.get(ListContainerView_.identificationBarcode));
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, ListContainerView> builder, String original) {
    if ("id".equals(original)) {
      return builder.getRoot().get(ListContainerView_.containerId);
    } else {
      return builder.getRoot().get(original);
    }
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, ListContainerView> builder, DateType type) {
    switch (type) {
      case ENTERED:
        return builder.getRoot().get(ListContainerView_.created);
      case UPDATE:
        return builder.getRoot().get(ListContainerView_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public SingularAttribute<ListContainerView, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? ListContainerView_.creator : ListContainerView_.lastModifier;
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, ListContainerView> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    Join<ListContainerView, SequencingContainerModel> modelJoin =
        builder.getJoin(builder.getRoot(), ListContainerView_.model);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(modelJoin.get(SequencingContainerModel_.platformType), platformType));
  }

  @Override
  public void restrictPaginationByKitName(QueryBuilder<?, ListContainerView> builder, String query,
      Consumer<String> errorHandler) {
    Join<ListContainerView, KitDescriptor> clusterJoin =
        builder.getJoin(builder.getRoot(), ListContainerView_.clusteringKit);
    Join<ListContainerView, KitDescriptor> multiplexingJoin =
        builder.getJoin(builder.getRoot(), ListContainerView_.multiplexingKit);
    builder.addTextRestriction(
        Arrays.asList(clusterJoin.get(KitDescriptor_.name), multiplexingJoin.get(KitDescriptor_.name)), query);
  }

  @Override
  public void restrictPaginationByIndex(QueryBuilder<?, ListContainerView> builder, String query,
      Consumer<String> errorHandler) {
    Join<ListContainerView, PartitionImpl> partitionJoin =
        builder.getJoin(builder.getRoot(), ListContainerView_.partitions);
    Join<PartitionImpl, PoolImpl> poolJoin = builder.getJoin(partitionJoin, PartitionImpl_.pool);
    Join<PoolImpl, PoolElement> elementJoin = builder.getJoin(poolJoin, PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquotJoin = builder.getJoin(elementJoin, PoolElement_.aliquot);
    Join<ListLibraryAliquotView, ParentLibrary> libraryJoin =
        builder.getJoin(aliquotJoin, ListLibraryAliquotView_.parentLibrary);
    Join<ParentLibrary, LibraryIndex> index1 = builder.getJoin(libraryJoin, ParentLibrary_.index1);
    Join<ParentLibrary, LibraryIndex> index2 = builder.getJoin(libraryJoin, ParentLibrary_.index2);
    builder.addTextRestriction(Arrays.asList(
        index1.get(LibraryIndex_.name), index1.get(LibraryIndex_.sequence),
        index2.get(LibraryIndex_.name), index2.get(LibraryIndex_.sequence)),
        query);
  }

}
