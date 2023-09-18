package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Session;

public class LongQueryBuilder<T> extends QueryBuilder<Long, T> {

  public LongQueryBuilder(Session session, Class<T> entityClass) {
    super(session, entityClass, Long.class);
  }

  public long getCount() {
    getQuery().select(getCriteriaBuilder().countDistinct(getRoot()));
    return getSingleResultOrNull();
  }

}
