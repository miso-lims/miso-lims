package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Index_;
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

  private static final List<SingularAttribute<? super ListContainerView, String>> SEARCH_PROPERTIES =
      Arrays.asList(ListContainerView_.identificationBarcode);

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
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
  public List<SingularAttribute<? super ListContainerView, String>> getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Path<?> propertyForSortColumn(Root<ListContainerView> root, String original) {
    if ("id".equals(original)) {
      return root.get(ListContainerView_.CONTAINER_ID);
    } else {
      return root.get(original);
    }
  }

  @Override
  public Path<?> propertyForDate(Root<ListContainerView> root, DateType type) {
    switch (type) {
      case ENTERED:
        return root.get(ListContainerView_.created);
      case UPDATE:
        return root.get(ListContainerView_.lastModified);
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
    Join<ParentLibrary, Index> index1 = builder.getJoin(libraryJoin, ParentLibrary_.index1);
    Join<ParentLibrary, Index> index2 = builder.getJoin(libraryJoin, ParentLibrary_.index2);
    builder.addTextRestriction(Arrays.asList(
        index1.get(Index_.name), index1.get(Index_.sequence),
        index2.get(Index_.name), index2.get(Index_.sequence)),
        query);
  }

}
