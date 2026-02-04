package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.HashSet;
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
        assertEquals("%", field.getUnits());
      }
    }

    assertTrue("Should have Flow Cell Lot field", hasFlowCell);
    assertTrue("Should have PhiX % field", hasPhiX);
  }

}
