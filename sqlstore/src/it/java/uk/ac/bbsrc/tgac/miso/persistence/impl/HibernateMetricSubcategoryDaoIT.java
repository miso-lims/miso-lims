package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;

public class HibernateMetricSubcategoryDaoIT
    extends AbstractHibernateSaveDaoTest<MetricSubcategory, HibernateMetricSubcategoryDao> {

  public HibernateMetricSubcategoryDaoIT() {
    super(MetricSubcategory.class, 3L, 4);
  }

  @Override
  public HibernateMetricSubcategoryDao constructTestSubject() {
    HibernateMetricSubcategoryDao sut = new HibernateMetricSubcategoryDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public MetricSubcategory getCreateItem() {
    MetricSubcategory item = new MetricSubcategory();
    item.setAlias("New");
    item.setCategory(MetricCategory.FULL_DEPTH_SEQUENCING);
    return item;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<MetricSubcategory, String> getUpdateParams() {
    return new UpdateParameters<>(2L, MetricSubcategory::getAlias, MetricSubcategory::setAlias, "Changed");
  }

  @Test
  public void testGetByAliasAndCategory() throws Exception {
    final String alias = "Nucleic Acid Isolation";
    final MetricCategory category = MetricCategory.EXTRACTION;
    MetricSubcategory item = getTestSubject().getByAliasAndCategory(alias, category);
    assertNotNull(item);
    assertEquals(alias, item.getAlias());
    assertEquals(category, item.getCategory());
  }

  @Test
  public void testGetUsage() throws Exception {
    MetricSubcategory item = (MetricSubcategory) currentSession().get(MetricSubcategory.class, 3L);
    assertEquals(1L, getTestSubject().getUsage(item));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateMetricSubcategoryDao::listByIdList, Arrays.asList(2L, 3L));
  }

}
