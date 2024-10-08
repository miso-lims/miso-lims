package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.junit.Test;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;

public class HibernatePipelineDaoIT extends AbstractHibernateSaveDaoTest<Pipeline, HibernatePipelineDao> {

  public HibernatePipelineDaoIT() {
    super(Pipeline.class, 1L, 2);
  }

  @Override
  public HibernatePipelineDao constructTestSubject() {
    HibernatePipelineDao sut = new HibernatePipelineDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public Pipeline getCreateItem() {
    Pipeline pipeline = new Pipeline();
    pipeline.setAlias("New Pipeline");
    return pipeline;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Pipeline, String> getUpdateParams() {
    return new UpdateParameters<>(2L, Pipeline::getAlias, Pipeline::setAlias, "Changed");
  }

  @Test
  public void testGetByAlias() throws Exception {
    testGetBy(HibernatePipelineDao::getByAlias, "Special", Pipeline::getAlias);
  }

  @Test
  public void testGetUsage() throws Exception {
    testGetUsage(HibernatePipelineDao::getUsage, 1L, 2L);
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernatePipelineDao::listByIdList, Lists.newArrayList(1L, 2L));
  }

}
