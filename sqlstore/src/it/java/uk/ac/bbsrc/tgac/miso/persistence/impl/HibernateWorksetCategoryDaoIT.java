package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetCategory;

public class HibernateWorksetCategoryDaoIT
    extends AbstractHibernateSaveDaoTest<WorksetCategory, HibernateWorksetCategoryDao> {

  public HibernateWorksetCategoryDaoIT() {
    super(WorksetCategory.class, 1L, 2);
  }

  @Override
  public HibernateWorksetCategoryDao constructTestSubject() {
    HibernateWorksetCategoryDao sut = new HibernateWorksetCategoryDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public WorksetCategory getCreateItem() {
    WorksetCategory category = new WorksetCategory();
    category.setAlias("Category C");
    return category;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<WorksetCategory, String> getUpdateParams() {
    return new UpdateParameters<>(2L, WorksetCategory::getAlias, WorksetCategory::setAlias, "changed");
  }

  @Test
  public void testGetByAlias() throws Exception {
    testGetBy(HibernateWorksetCategoryDao::getByAlias, "Category B", WorksetCategory::getAlias);
  }

  @Test
  public void testGetUsage() throws Exception {
    testGetUsage(HibernateWorksetCategoryDao::getUsage, 1L, 2L);
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateWorksetCategoryDao::listByIdList, Arrays.asList(1L, 2L));
  }

}
