package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetStage;

public class HibernateWorksetStageDaoIT extends AbstractHibernateSaveDaoTest<WorksetStage, HibernateWorksetStageDao> {

  public HibernateWorksetStageDaoIT() {
    super(WorksetStage.class, 3L, 3);
  }

  @Override
  public HibernateWorksetStageDao constructTestSubject() {
    HibernateWorksetStageDao sut = new HibernateWorksetStageDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public WorksetStage getCreateItem() {
    WorksetStage stage = new WorksetStage();
    stage.setAlias("New Stage");
    return stage;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<WorksetStage, String> getUpdateParams() {
    return new UpdateParameters<>(2L, WorksetStage::getAlias, WorksetStage::setAlias, "changed");
  }

  @Test
  public void testGetByAlias() throws Exception {
    testGetBy(HibernateWorksetStageDao::getByAlias, "Extraction", WorksetStage::getAlias);
  }

  @Test
  public void testGetUsage() throws Exception {
    testGetUsage(HibernateWorksetStageDao::getUsage, 2L, 2L);
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateWorksetStageDao::listByIdList, Arrays.asList(2L, 3L));
  }

}
