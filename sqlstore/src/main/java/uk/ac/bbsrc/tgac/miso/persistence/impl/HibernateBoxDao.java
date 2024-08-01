package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition_;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize_;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse_;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView_;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.BoxStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBoxDao extends HibernateProviderDao<Box>
    implements BoxStore, JpaCriteriaPaginatedDataSource<Box, BoxImpl> {

  public HibernateBoxDao() {
    super(Box.class, BoxImpl.class);
  }

  protected static final List<SingularAttribute<? super BoxImpl, String>> IDENTIFIER_PROPERTIES =
      Arrays.asList(BoxImpl_.name, BoxImpl_.alias, BoxImpl_.identificationBarcode);

  @Override
  public Boxable getBoxable(BoxableId id) {
    Class<?> clazz = id.getTargetType().getPersistClass();
    return (Boxable) currentSession().get(clazz, id.getTargetId());
  }

  @Override
  public void saveBoxable(Boxable boxable) {
    currentSession().update(boxable);
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
    return getBy(BoxImpl_.ALIAS, alias);
  }

  @Override
  public List<Box> listByIdList(List<Long> idList) throws IOException {
    return listByIdList(BoxImpl_.BOX_ID, idList);
  }

  @Override
  public List<Box> getBySearch(String search) {
    if (search == null) {
      throw new NullPointerException("No search String provided");
    }

    QueryBuilder<Box, BoxImpl> builder = getQueryBuilder();
    builder.addPredicate(builder.getCriteriaBuilder()
        .or(builder.getCriteriaBuilder().equal(builder.getRoot().get(BoxImpl_.identificationBarcode), search),
            builder.getCriteriaBuilder().equal(builder.getRoot().get(BoxImpl_.name), search),
            builder.getCriteriaBuilder().equal(builder.getRoot().get(BoxImpl_.alias), search)));
    return builder.getResultList();
  }

  private static String getMostSimilarProperty(Box box, String search) {
    List<String> properties = new ArrayList<>(Arrays.asList(box.getAlias(), box.getName()));
    if (box.getIdentificationBarcode() != null) {
      properties.add(box.getIdentificationBarcode());
    }
    return properties.stream().map(String::toLowerCase).filter(p -> p.indexOf(search) >= 0)
        .min(Comparator.comparingInt(p -> p.indexOf(search))).orElse("");
  }

  @Override
  public List<Box> getByPartialSearch(String search, boolean onlyMatchBeginning) {
    if (search == null) {
      throw new NullPointerException("No search String provided");
    }

    String pattern = onlyMatchBeginning ? search + '%' : '%' + search + '%';
    QueryBuilder<Box, BoxImpl> builder = getQueryBuilder();
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().like(builder.getRoot().get(BoxImpl_.identificationBarcode), pattern),
        builder.getCriteriaBuilder().like(builder.getRoot().get(BoxImpl_.name), pattern),
        builder.getCriteriaBuilder().like(builder.getRoot().get(BoxImpl_.alias), pattern)));
    List<Box> results = builder.getResultList();

    results.sort((Box b1, Box b2) -> {
      String p1 = getMostSimilarProperty(b1, search.toLowerCase());
      String p2 = getMostSimilarProperty(b2, search.toLowerCase());
      if (p1.indexOf(search.toLowerCase()) == p2.indexOf(search.toLowerCase())) {
        if (p1.length() == p2.length()) {
          return b1.getAlias().compareTo(b2.getAlias());
        }
        return p1.length() - p2.length();
      }
      return p1.indexOf(search.toLowerCase()) - p2.indexOf(search.toLowerCase());
    });
    return results;
  }

  @Override
  public void removeBoxableFromBox(BoxableView boxable) throws IOException {
    if (boxable.getBoxId() == null) {
      return;
    }
    Box box = get(boxable.getBoxId());
    box.getBoxPositions().remove(boxable.getBoxPosition());
    currentSession().update(box);
    // flush required to avoid constraint violation incase item is immediately added to another box or
    // the same one
    // NOTE: this flush will cause ALL Hibernate-managed items to be saved to the db in their current
    // state, even if their `save` method
    // hasn't explicitly been called yet
    currentSession().flush();
  }

  @Override
  public long save(Box box) throws IOException {
    if (!box.isSaved()) {
      return (long) currentSession().save(box);
    } else {
      currentSession().update(box);
      // flush required to avoid constraint violation incase removed items are immediately added to
      // another box or the same one
      // NOTE: this flush will cause ALL Hibernate-managed items to be saved to the db in their current
      // state, even if their `save` method
      // hasn't explicitly been called yet
      currentSession().flush();
      return box.getId();
    }
  }

  @Override
  public BoxableView getBoxableView(BoxableId id) throws IOException {
    return (BoxableView) currentSession().get(id.getTargetType().getViewClass(), id.getTargetId());
  }

  @Override
  public List<BoxableView> getBoxableViewsByBarcodeList(Collection<String> barcodes) throws IOException {
    if (barcodes == null || barcodes.isEmpty()) {
      return Collections.emptyList();
    }

    List<BoxableView> results = new ArrayList<>();
    for (EntityType entityType : EntityType.values()) {
      QueryBuilder<BoxableView, ? extends BoxableView> builder =
          new QueryBuilder<>(currentSession(), entityType.getViewClass(), BoxableView.class);
      builder.addInPredicate(builder.getRoot().get(BoxableView_.identificationBarcode), barcodes);
      List<BoxableView> partialResults = builder.getResultList();
      results.addAll(partialResults);
    }
    return results;
  }

  @Override
  public List<BoxableView> getBoxableViewsByIdList(Collection<BoxableId> ids) throws IOException {
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }

    List<BoxableView> results = new ArrayList<>();
    for (EntityType entityType : EntityType.values()) {
      List<Long> filteredIds = ids.stream()
          .filter(x -> x.getTargetType() == entityType)
          .map(BoxableId::getTargetId)
          .collect(Collectors.toList());
      if (!filteredIds.isEmpty()) {
        QueryBuilder<BoxableView, ? extends BoxableView> builder =
            new QueryBuilder<>(currentSession(), entityType.getViewClass(), BoxableView.class);
        builder.addInPredicate(builder.getRoot().get(entityType.getIdProperty()), filteredIds);
        List<BoxableView> partialResults = builder.getResultList();

        results.addAll(partialResults);
      }
    }
    return results;
  }

  @Override
  public List<BoxableView> getBoxContents(long boxId) throws IOException {
    List<BoxableView> results = new ArrayList<>();
    for (EntityType entityType : EntityType.values()) {
      QueryBuilder<BoxableView, ? extends BoxableView> builder =
          new QueryBuilder<>(currentSession(), entityType.getViewClass(), BoxableView.class);
      Join<? extends BoxableView, BoxPosition> boxPosition =
          builder.getSingularJoin(builder.getRoot(), "boxPosition", BoxPosition.class);
      Join<BoxPosition, BoxImpl> box = builder.getJoin(boxPosition, BoxPosition_.box);
      builder.addPredicate(builder.getCriteriaBuilder().equal(box.get(BoxImpl_.boxId), boxId));
      List<BoxableView> partialResults = builder.getResultList();
      results.addAll(partialResults);
    }
    return results;
  }

  @Override
  public List<BoxableView> getBoxableViewsBySearch(String search) {
    if (search == null) {
      throw new NullPointerException("No search String provided");
    }

    List<BoxableView> results = new ArrayList<>();
    for (EntityType entityType : EntityType.values()) {
      QueryBuilder<BoxableView, ? extends BoxableView> builder =
          new QueryBuilder<>(currentSession(), entityType.getViewClass(), BoxableView.class);
      builder.addPredicate(builder.getCriteriaBuilder().and(
          builder.getCriteriaBuilder().or(
              builder.getCriteriaBuilder().equal(builder.getRoot().get(BoxableView_.identificationBarcode), search),
              builder.getCriteriaBuilder().equal(builder.getRoot().get(BoxableView_.name), search),
              builder.getCriteriaBuilder().equal(builder.getRoot().get(BoxableView_.alias), search)),
          builder.getCriteriaBuilder().equal(builder.getRoot().get(BoxableView_.discarded), false)));
      List<BoxableView> partialResults = builder.getResultList();
      results.addAll(partialResults);
    }
    return results;
  }

  @Override
  public String getFriendlyName() {
    return "Box";
  }

  @Override
  public SingularAttribute<? super BoxImpl, ?> getIdProperty() {
    return BoxImpl_.boxId;
  }

  @Override
  public Class<BoxImpl> getEntityClass() {
    return BoxImpl.class;
  }

  @Override
  public Class<Box> getResultClass() {
    return Box.class;
  }

  @Override
  public List<SingularAttribute<? super BoxImpl, String>> getIdentifierProperties() {
    return IDENTIFIER_PROPERTIES;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<BoxImpl> root) {
    return Arrays.asList(root.get(BoxImpl_.name), root.get(BoxImpl_.alias),
        root.get(BoxImpl_.identificationBarcode), root.get(BoxImpl_.locationBarcode));
  }

  @Override
  public Path<?> propertyForDate(Root<BoxImpl> root, DateType type) {
    switch (type) {
      case ENTERED:
        return root.get(BoxImpl_.creationTime);
      case UPDATE:
        return root.get(BoxImpl_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, BoxImpl> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(BoxImpl_.boxId);
      case "sizeId":
        return builder.getJoin(builder.getRoot(), BoxImpl_.size).get(BoxSize_.id);
      case "useId":
        return builder.getJoin(builder.getRoot(), BoxImpl_.use).get(BoxUse_.id);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<? super BoxImpl, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? BoxImpl_.creator : BoxImpl_.lastModifier;
  }

  @Override
  public void restrictPaginationByBoxUse(QueryBuilder<?, BoxImpl> builder, long id, Consumer<String> errorHandler) {
    Join<BoxImpl, BoxUse> use = builder.getJoin(builder.getRoot(), BoxImpl_.use);
    builder.addPredicate(builder.getCriteriaBuilder().equal(use.get(BoxUse_.id), id));
  }

  @Override
  public void restrictPaginationByFreezer(QueryBuilder<?, BoxImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<BoxImpl, BoxPosition> boxPositionJoin = builder.getJoin(builder.getRoot(), BoxImpl_.boxPositions);
    builder.addFreezerPredicate(boxPositionJoin, query);
  }

  @Override
  public void restrictPaginationByBoxType(QueryBuilder<?, BoxImpl> builder, BoxType boxType,
      Consumer<String> errorHandler) {
    Join<BoxImpl, BoxSize> size = builder.getJoin(builder.getRoot(), BoxImpl_.size);
    builder.addPredicate(builder.getCriteriaBuilder().equal(size.get(BoxSize_.boxType), boxType));
  }

  @Override
  public void restrictPaginationByBarcode(QueryBuilder<?, BoxImpl> builder, String barcode,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot().get(BoxImpl_.identificationBarcode), barcode);;
  }

}
