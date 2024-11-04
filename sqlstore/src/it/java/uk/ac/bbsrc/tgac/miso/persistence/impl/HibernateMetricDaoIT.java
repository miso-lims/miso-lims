package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.ThresholdType;

public class HibernateMetricDaoIT extends AbstractHibernateSaveDaoTest<Metric, HibernateMetricDao> {

  public HibernateMetricDaoIT() {
    super(Metric.class, 2L, 3);
  }

  @Override
  public HibernateMetricDao constructTestSubject() {
    HibernateMetricDao sut = new HibernateMetricDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public Metric getCreateItem() {
    Metric metric = new Metric();
    metric.setAlias("New Metric");
    metric.setCategory(MetricCategory.ANALYSIS_REVIEW);
    metric.setThresholdType(ThresholdType.BETWEEN);
    metric.setUnits("%");
    return metric;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Metric, String> getUpdateParams() {
    return new UpdateParameters<>(2L, Metric::getAlias, Metric::setAlias, "changed");
  }

  @Test
  public void testGetUsage() throws Exception {
    Metric metric = (Metric) currentSession().get(Metric.class, 1L);
    assertEquals(2L, getTestSubject().getUsage(metric));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateMetricDao::listByIdList, Arrays.asList(1L, 2L, 3L));
  }

}
