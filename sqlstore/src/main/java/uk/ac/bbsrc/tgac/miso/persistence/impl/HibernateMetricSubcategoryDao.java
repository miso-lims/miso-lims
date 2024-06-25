package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric_;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.persistence.MetricSubcategoryDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateMetricSubcategoryDao extends HibernateSaveDao<MetricSubcategory> implements MetricSubcategoryDao {

  public HibernateMetricSubcategoryDao() {
    super(MetricSubcategory.class);
  }

  @Override
  public MetricSubcategory getByAliasAndCategory(String alias, MetricCategory category) throws IOException {
    QueryBuilder<MetricSubcategory, MetricSubcategory> builder =
        new QueryBuilder<>(currentSession(), MetricSubcategory.class, MetricSubcategory.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(MetricSubcategory_.alias), alias));
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(MetricSubcategory_.category), category));
    return builder.getSingleResultOrNull();
  }

  @Override
  public long getUsage(MetricSubcategory subcategory) throws IOException {
    return getUsageBy(Metric.class, Metric_.SUBCATEGORY, subcategory);
  }

  @Override
  public List<MetricSubcategory> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(MetricSubcategory_.SUBCATEGORY_ID, ids);
  }

}
