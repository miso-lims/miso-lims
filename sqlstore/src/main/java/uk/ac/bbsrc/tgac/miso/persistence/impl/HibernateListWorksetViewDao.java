package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView_;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ListWorksetViewStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateListWorksetViewDao
    implements ListWorksetViewStore, JpaCriteriaPaginatedDataSource<ListWorksetView, ListWorksetView> {

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
    return "Workset";
  }

  @Override
  public SingularAttribute<ListWorksetView, ?> getIdProperty() {
    return ListWorksetView_.worksetId;
  }

  @Override
  public Class<ListWorksetView> getEntityClass() {
    return ListWorksetView.class;
  }

  @Override
  public Class<ListWorksetView> getResultClass() {
    return ListWorksetView.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<ListWorksetView> root) {
    return Arrays.asList(root.get(ListWorksetView_.alias), root.get(ListWorksetView_.description));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, ListWorksetView> builder, DateType type) {
    switch (type) {
      case ENTERED:
        return builder.getRoot().get(ListWorksetView_.created);
      case UPDATE:
        return builder.getRoot().get(ListWorksetView_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, ListWorksetView> builder, String original) {
    if ("id".equals(original)) {
      return builder.getRoot().get(ListWorksetView_.worksetId);
    } else {
      return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<ListWorksetView, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? ListWorksetView_.creator : ListWorksetView_.lastModifier;
  }

  @Override
  public List<ListWorksetView> listBySearch(String query) throws IOException {
    if (query == null) {
      throw new NullPointerException("No query string provided");
    }

    QueryBuilder<ListWorksetView, ListWorksetView> builder = getQueryBuilder();
    builder
        .addPredicate(builder.getCriteriaBuilder().like(builder.getRoot().get(ListWorksetView_.alias), query + '%'));
    return builder.getResultList();
  }

  @Override
  public void restrictPaginationByCategory(QueryBuilder<?, ListWorksetView> builder, String query,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot().get(ListWorksetView_.category), query);
  }

  @Override
  public void restrictPaginationByStage(QueryBuilder<?, ListWorksetView> builder, String query,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot().get(ListWorksetView_.stage), query);
  }

}
