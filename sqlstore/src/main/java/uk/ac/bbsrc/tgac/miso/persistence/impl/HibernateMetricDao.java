package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric_;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.persistence.MetricDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateMetricDao extends HibernateSaveDao<Metric> implements MetricDao {

  public HibernateMetricDao() {
    super(Metric.class, Metric.class);
  }

  @Override
  public Metric getByAliasAndCategory(String alias, MetricCategory category, MetricSubcategory subcategory)
      throws IOException {
    QueryBuilder<Metric, Metric> builder = new QueryBuilder<>(currentSession(), Metric.class, Metric.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Metric_.alias), alias));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Metric_.category), category));
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(Metric_.subcategory), subcategory),
        builder.getCriteriaBuilder().isNull(builder.getRoot().get(Metric_.subcategory))));
    return builder.getSingleResultOrNull();
  }

  @Override
  public long getUsage(Metric metric) throws IOException {
    LongQueryBuilder<Assay> builder = new LongQueryBuilder<>(currentSession(), Assay.class);
    Join<Assay, AssayMetric> metricJoin = builder.getJoin(builder.getRoot(), Assay_.assayMetrics);
    builder.addPredicate(builder.getCriteriaBuilder().equal(metricJoin.get(AssayMetric_.metric), metric));
    return builder.getCount();
  }

  @Override
  public List<Metric> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(Metric_.METRIC_ID, ids);
  }

}
