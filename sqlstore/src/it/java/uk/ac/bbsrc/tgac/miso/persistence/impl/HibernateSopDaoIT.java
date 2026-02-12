package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.SopField;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;

public class HibernateSopDaoIT extends AbstractHibernateSaveDaoTest<Sop, HibernateSopDao> {

  public HibernateSopDaoIT() {
    super(Sop.class, 1L, 6);
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
    sop.setAlias("Integration Test SOP");
    sop.setVersion("1.0");
    sop.setCategory(SopCategory.RUN);
    sop.setUrl("http://test.com/integration-sop");
    sop.setArchived(false);
    return sop;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Sop, String> getUpdateParams() {
    return new UpdateParameters<>(1L, Sop::getAlias, Sop::setAlias, "Changed SOP Alias");
  }

  @Test
  public void testSaveSopWithFields() throws Exception {
    Sop sop = new Sop();
    sop.setAlias("Integration Test SOP");
    sop.setVersion("1.0");
    sop.setCategory(SopCategory.RUN);
    sop.setUrl("http://test.com/integration-sop");
    sop.setArchived(false);

    SopField field1 = new SopField();
    field1.setName("Flow Cell Lot");
    field1.setFieldType(SopField.FieldType.TEXT);
    field1.setSop(sop);

    SopField field2 = new SopField();
    field2.setName("PhiX %");
    field2.setUnits("%");
    field2.setFieldType(SopField.FieldType.NUMBER);
    field2.setSop(sop);

    Set<SopField> fields = new HashSet<>();
    fields.add(field1);
    fields.add(field2);
    sop.setSopFields(fields);

    long savedId = getTestSubject().create(sop);
    clearSession();

    Sop loaded = currentSession().get(Sop.class, savedId);
    assertNotNull(loaded);
    assertNotNull(loaded.getSopFields());
    assertEquals(2, loaded.getSopFields().size());

    boolean hasFlowCell = false;
    boolean hasPhiX = false;

    for (SopField field : loaded.getSopFields()) {
      if ("Flow Cell Lot".equals(field.getName())) {
        hasFlowCell = true;
        assertEquals(SopField.FieldType.TEXT, field.getFieldType());
      } else if ("PhiX %".equals(field.getName())) {
        hasPhiX = true;
        assertEquals(SopField.FieldType.NUMBER, field.getFieldType());
      }
    }

    assertTrue("Should have Flow Cell Lot field", hasFlowCell);
    assertTrue("Should have PhiX % field", hasPhiX);
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
    testListByIdList(HibernateSopDao::listByIdList, Arrays.asList(3L, 4L));
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
}

