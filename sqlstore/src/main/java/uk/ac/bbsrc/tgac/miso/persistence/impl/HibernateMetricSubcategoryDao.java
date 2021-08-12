package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
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
    return (MetricSubcategory) currentSession().createCriteria(getEntityClass())
        .add(Restrictions.eq("alias", alias))
        .add(Restrictions.eq("category", category))
        .uniqueResult();
  }

  @Override
  public long getUsage(MetricSubcategory subcategory) throws IOException {
    return getUsageBy(Metric.class, "subcategory", subcategory);
  }

  @Override
  public List<MetricSubcategory> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("subcategoryId", ids);
  }

}
