package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.criterion.Criterion;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
public interface HibernatePaginatedDataSource<T, Filter extends PaginationFilter> extends BaseHibernatePaginatedDataSource<T, Filter> {

  String[] getSearchProperties();

  @Override
  default Criterion searchRestrictions(String query) {
    return DbUtils.searchRestrictions(query, getSearchProperties());
  }

}
