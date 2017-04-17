package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.criterion.Criterion;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
public interface HibernatePaginatedDataSource<T> extends BaseHibernatePaginatedDataSource<T> {

  String[] getSearchProperties();

  @Override
  default Criterion searchRestrictions(String query) {
    return DbUtils.searchRestrictions(query, getSearchProperties());
  }

}
