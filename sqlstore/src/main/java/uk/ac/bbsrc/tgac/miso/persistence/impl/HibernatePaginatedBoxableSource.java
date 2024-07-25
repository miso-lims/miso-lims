package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

public interface HibernatePaginatedBoxableSource<T extends Boxable> extends HibernatePaginatedDataSource<T> {

  public final static String[] SEARCH_PROPS =
      new String[] {"name", "alias", "identificationBarcode", "locationBarcode"};

  public final static String[] BOX_SEARCH_PROPERTIES = LimsUtils.prefix("box.", SEARCH_PROPS);

  @Override
  default void restrictPaginationByBox(Criteria criteria, String query, Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      criteria.createAlias("boxPosition", "boxPosition", JoinType.LEFT_OUTER_JOIN)
          .add(Restrictions.isNull("boxPosition.box"));
    } else {
      criteria.createAlias("boxPosition", "boxPosition")
          .createAlias("boxPosition.box", "box")
          .add(DbUtils.textRestriction(query, BOX_SEARCH_PROPERTIES));
    }
  }

  @Override
  public default void restrictPaginationByFreezer(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.createAlias("boxPosition", "boxPosition", JoinType.LEFT_OUTER_JOIN)
        .createAlias("boxPosition.box", "box", JoinType.LEFT_OUTER_JOIN);
    DbUtils.restrictPaginationByFreezer(criteria, query, "box.storageLocation");
  }

  @Override
  public default void restrictPaginationByDate(Criteria criteria, Date start, Date end, DateType type,
      Consumer<String> errorHandler) {
    if (type == DateType.RECEIVE) {
      DbUtils.restrictPaginationByReceiptTransferDate(criteria, start, end);
    } else if (type == DateType.DISTRIBUTED) {
      DbUtils.restrictPaginationByDistributionTransferDate(criteria, start, end);
    } else {
      HibernatePaginatedDataSource.super.restrictPaginationByDate(criteria, start, end, type, errorHandler);
    }
  }

  @Override
  public default void restrictPaginationByBarcode(Criteria criteria, String barcode, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(barcode, "identificationBarcode"));
  }

}
