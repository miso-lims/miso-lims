package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;

public class HibernateScientificNameDaoIT
    extends AbstractHibernateSaveDaoTest<ScientificName, HibernateScientificNameDao> {

  public HibernateScientificNameDaoIT() {
    super(ScientificName.class, 1L, 3);
  }

  @Override
  public HibernateScientificNameDao constructTestSubject() {
    HibernateScientificNameDao sut = new HibernateScientificNameDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public ScientificName getCreateItem() {
    ScientificName item = new ScientificName();
    item.setAlias("New Sci Name");
    return item;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<ScientificName, String> getUpdateParams() {
    return new UpdateParameters<>(1L, ScientificName::getAlias, ScientificName::setAlias, "Updated Alias");
  }

  @Test
  public void testGetByAlias() throws Exception {
    testGetBy(HibernateScientificNameDao::getByAlias, "Homo sapiens", ScientificName::getAlias);
  }

  @Test
  public void testGetUsageBySamples() throws Exception {
    testGetUsage(HibernateScientificNameDao::getUsageBySamples, 1L, 25L);
  }

  @Test
  public void testGetUsageByReferenceGenomes() throws Exception {
    testGetUsage(HibernateScientificNameDao::getUsageByReferenceGenomes, 1L, 2L);
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateScientificNameDao::listByIdList, Arrays.asList(2L, 1L, 3L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(getTestSubject()::listByIdList);
  }

}
