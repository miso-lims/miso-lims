package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxPosition_;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl_;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public interface JpaCriteriaPaginatedBoxableSource<R extends Boxable, T extends R>
    extends JpaCriteriaPaginatedDataSource<R, T> {

  public static final List<SingularAttribute<? super BoxImpl, String>> SEARCH_PROPERTIES =
      Arrays.asList(BoxImpl_.name, BoxImpl_.alias, BoxImpl_.identificationBarcode, BoxImpl_.locationBarcode);

  @Override
  default void restrictPaginationByBox(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    Join<T, ? extends AbstractBoxPosition> join = builder.getSingularJoin(builder.getRoot(), "boxPosition", null);
    if (LimsUtils.isStringBlankOrNull(query)) {
      builder.addPredicate(builder.getCriteriaBuilder().isNull(join.get(AbstractBoxPosition_.box)));
    } else {
      Join<? extends AbstractBoxPosition, BoxImpl> boxJoin = builder.getJoin(join, AbstractBoxPosition_.box);
      List<Path<String>> searchProperties = new ArrayList<>();
      for (SingularAttribute<? super BoxImpl, String> property : SEARCH_PROPERTIES) {
        searchProperties.add(boxJoin.get(property));
      }
      builder.addTextRestriction(searchProperties, query);
    }
  }

  @Override
  public default void restrictPaginationByFreezer(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    Join<T, ? extends AbstractBoxPosition> join = builder.getSingularJoin(builder.getRoot(), "boxPosition", null);
    builder.addFreezerPredicate(join, query);
  }

  @Override
  public default void restrictPaginationByDate(QueryBuilder<?, T> builder, Date start, Date end, DateType type,
      Consumer<String> errorHandler) {
    if (type == DateType.RECEIVE) {
      builder.addReceiptTransferDatePredicate(start, end);
    } else if (type == DateType.DISTRIBUTED) {
      builder.addDistributionTransferDatePredicate(start, end);
    } else {
      JpaCriteriaPaginatedDataSource.super.restrictPaginationByDate(builder, start, end, type, errorHandler);
    }
  }

  @Override
  public default void restrictPaginationByBarcode(QueryBuilder<?, T> builder, String barcode,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot().get("identificationBarcode"), barcode);
  }

}
