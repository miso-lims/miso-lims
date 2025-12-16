package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.SopField;

public class HibernateSopDaoIT extends AbstractHibernateSaveDaoTest<Sop, HibernateSopDao> {

  public HibernateSopDaoIT() {
    super(Sop.class, 1L, 5);
  }

  @Override
  public HibernateSopDao constructTestSubject() {
    HibernateSopDao sut = new HibernateSopDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public Sop getCreateItem() {
    Sop sop = new Sop();
    sop.setAlias("Test SOP");
    sop.setVersion("1.0");
    sop.setCategory(SopCategory.SAMPLE);
    sop.setUrl("http://sops.test.com/test_sop");
    sop.setArchived(false);
    return sop;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Sop, String> getUpdateParams() {
    return new UpdateParameters<>(2L, Sop::getAlias, Sop::setAlias, "Updated Alias");
  }

  @Test
  public void testGetByAliasAndVersion() throws Exception {
    SopCategory category = SopCategory.SAMPLE;
    String alias = "Sample SOP 1";
    String version = "1.0";
    Sop sop = getTestSubject().get(category, alias, version);
    assertNotNull(sop);
    assertEquals(category, sop.getCategory());
    assertEquals(alias, sop.getAlias());
    assertEquals(version, sop.getVersion());
  }

  @Test
  public void testListByCategory() throws Exception {
    List<Sop> sops = getTestSubject().listByCategory(SopCategory.LIBRARY);
    assertNotNull(sops);
    assertEquals(2, sops.size());
    for (Sop sop : sops) {
      assertEquals(SopCategory.LIBRARY, sop.getCategory());
    }
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateSopDao::listByIdList, Lists.newArrayList(3L, 4L));
  }

  @Test
  public void testGetUsageBySamples() throws Exception {
    testGetUsage(HibernateSopDao::getUsageBySamples, 1L, 1L);
  }

  @Test
  public void testGetUsageByLibraries() throws Exception {
    testGetUsage(HibernateSopDao::getUsageByLibraries, 3L, 1L);
  }

  @Test
  public void testGetUsageByRuns() throws Exception {
    testGetUsage(HibernateSopDao::getUsageByRuns, 5L, 1L);
  }

  @Test
  public void testSaveSopWithFields() throws Exception {
    Sop sop = new Sop();
    sop.setAlias("Integration Test SOP");
    sop.setVersion("1.0");
    sop.setCategory("Sequencing");
    sop.setArchived(false);

    SopField field1 = new SopField();
    field1.setName("Flow Cell Lot");
    field1.setFieldType(SopField.FieldType.TEXT);

    SopField field2 = new SopField();
    field2.setName("PhiX %");
    field2.setUnits("%");
    field2.setFieldType(SopField.FieldType.PERCENTAGE);

    sop.addSopField(field1);
    sop.addSopField(field2);

    long savedId = sut.create(sop);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Sop loaded = sut.get(savedId);
    assertNotNull(loaded);
    assertEquals(2, loaded.getSopFields().size());
  }

  @Test
  public void testDeleteSopCascadesToFields() throws Exception {
    Sop sop = new Sop();
    sop.setAlias("SOP To Delete");
    sop.setVersion("1.0");
    sop.setCategory("Sequencing");
    sop.setArchived(false);

    SopField field = new SopField();
    field.setName("Test Field");
    field.setFieldType(SopField.FieldType.TEXT);
    sop.addSopField(field);

    long sopId = sut.create(sop);
    sessionFactory.getCurrentSession().flush();

    Sop loaded = sut.get(sopId);
    Long fieldId = loaded.getSopFields().iterator().next().getId();

    sut.remove(loaded);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    assertNull(sut.get(sopId));
    SopField orphanField = (SopField) sessionFactory.getCurrentSession()
        .get(SopField.class, fieldId);
    assertNull(orphanField);
  }

}
