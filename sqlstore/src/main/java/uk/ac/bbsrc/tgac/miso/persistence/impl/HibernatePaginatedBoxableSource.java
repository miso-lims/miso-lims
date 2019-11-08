package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

public interface HibernatePaginatedBoxableSource<T extends Boxable> extends HibernatePaginatedDataSource<T> {

  public final static String[] BOX_SEARCH_PROPERTIES = LimsUtils.prefix("box.", HibernateBoxDao.SEARCH_PROPERTIES);

  @Override
  default void restrictPaginationByBox(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.createAlias("boxPosition", "boxPosition");
    criteria.createAlias("boxPosition.box", "box");
    criteria.add(DbUtils.searchRestrictions(name, false, BOX_SEARCH_PROPERTIES));
  }

  @Override
  public default void restrictPaginationByFreezer(Criteria criteria, String freezer, Consumer<String> errorHandler) {
    criteria.createAlias("boxPosition", "boxPosition")
        .createAlias("boxPosition.box", "box")
        .createAlias("box.storageLocation", "location1")
        .createAlias("location1.parentLocation", "location2", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location2.parentLocation", "location3", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location3.parentLocation", "location4", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location4.parentLocation", "location5", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location5.parentLocation", "location6", JoinType.LEFT_OUTER_JOIN)
        .add(Restrictions.or(
            Restrictions.and(Restrictions.eq("location1.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location1.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location2.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location2.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location3.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location3.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location4.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location4.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location5.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location5.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location6.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location6.alias", freezer, MatchMode.START))));
  }

}
