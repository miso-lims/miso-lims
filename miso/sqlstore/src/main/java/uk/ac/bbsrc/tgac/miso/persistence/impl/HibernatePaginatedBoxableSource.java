package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.function.Consumer;

import org.hibernate.Criteria;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

public interface HibernatePaginatedBoxableSource<T extends Boxable> extends HibernatePaginatedDataSource<T> {
  public final static String[] BOX_SEARCH_PROPERTIES = LimsUtils.prefix("box.", HibernateBoxDao.SEARCH_PROPERTIES);
  @Override
  default void restrictPaginationByBox(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.createAlias("boxPosition", "boxPosition");
    criteria.createAlias("boxPosition.box", "box");
    criteria.add(DbUtils.searchRestrictions(name, BOX_SEARCH_PROPERTIES));
  }
}
