package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
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
    return (Metric) currentSession().createCriteria(getEntityClass())
        .add(Restrictions.eq("alias", alias))
        .add(Restrictions.eq("category", category))
        .add(Restrictions.eqOrIsNull("subcategory", subcategory))
        .uniqueResult();
  }

  @Override
  public long getUsage(Metric metric) throws IOException {
    return (long) currentSession().createCriteria(Assay.class)
        .createAlias("assayMetrics", "assayMetric")
        .add(Restrictions.eq("assayMetric.metric", metric))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public List<Metric> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("metricId", ids);
  }

}
