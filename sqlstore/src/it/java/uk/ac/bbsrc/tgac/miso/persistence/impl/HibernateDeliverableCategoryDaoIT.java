package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;

public class HibernateDeliverableCategoryDaoIT
    extends AbstractHibernateSaveDaoTest<DeliverableCategory, HibernateDeliverableCategoryDao> {

  public HibernateDeliverableCategoryDaoIT() {
    super(DeliverableCategory.class, 1L, 2);
  }

  @Override
  public HibernateDeliverableCategoryDao constructTestSubject() {
    HibernateDeliverableCategoryDao sut = new HibernateDeliverableCategoryDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public DeliverableCategory getCreateItem() {
    DeliverableCategory category = new DeliverableCategory();
    category.setName("New Category");
    return category;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<DeliverableCategory, String> getUpdateParams() {
    return new UpdateParameters<>(1L, DeliverableCategory::getName, DeliverableCategory::setName, "Changed");
  }

  @Test
  public void testGetByName() throws Exception {
    testGetBy(HibernateDeliverableCategoryDao::getByName, "category2", DeliverableCategory::getName);
  }

  @Test
  public void testGetUsage() throws Exception {
    testGetUsage(HibernateDeliverableCategoryDao::getUsage, 1L, 2L);
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateDeliverableCategoryDao::listByIdList, Arrays.asList(1L, 2L));
  }

}
