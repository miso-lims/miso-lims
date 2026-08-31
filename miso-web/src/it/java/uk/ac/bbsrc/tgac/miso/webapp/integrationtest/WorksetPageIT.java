package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.assertFieldValues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.WorksetPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.WorksetPage.Field;

public class WorksetPageIT extends AbstractIT {

  @BeforeEach
  public void setup() {
    login();
  }

  private WorksetPage getWorksetPage(Long id) {
    return id == null ? WorksetPage.getForNew(getDriver(), getBaseUrl()) : WorksetPage.get(getDriver(), getBaseUrl(), id);
  }

  @Test
  public void testCreate() {
    WorksetPage page = getWorksetPage(null);
    Map<Field, String> fields = new HashMap<>();
    fields.put(Field.ALIAS, "Test Set");
    fields.put(Field.DESCRIPTION, "Test description");
    page.setFields(fields);

    assertEquals("", page.getField(Field.ID));
    assertFieldValues("changes pre-save", fields, page);
    WorksetPage savedPage = page.clickSave();
    assertNotNull(savedPage, "Workset should save successfully");
    assertFieldValues("changes post-save", fields, savedPage);

    Workset workset = (Workset) getSession().find(Workset.class, Long.valueOf(savedPage.getField(Field.ID)));
    assertEquals(fields.get(Field.ALIAS), workset.getAlias());
    assertEquals(fields.get(Field.DESCRIPTION), workset.getDescription());
  }

  @Test
  public void testEdit() {
    WorksetPage page = getWorksetPage(1L);
    Map<Field, String> fields = getWorksetOneFields();

    Map<Field, String> changes = new HashMap<>();
    changes.put(Field.ALIAS, "New Alias");
    changes.put(Field.DESCRIPTION, "New Desc");
    page.setFields(changes);
    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page);

    WorksetPage savedPage = page.clickSave();
    assertNotNull(savedPage, "Workset should save successfully");
    assertFieldValues("changes post-save", fields, savedPage);

    Workset workset = (Workset) getSession().find(Workset.class, Long.valueOf(savedPage.getField(Field.ID)));
    assertEquals(fields.get(Field.ALIAS), workset.getAlias());
    assertEquals(fields.get(Field.DESCRIPTION), workset.getDescription());
  }

  @Test
  public void testNullifyFields() {
    WorksetPage page = getWorksetPage(1L);
    Map<Field, String> fields = getWorksetOneFields();
    assertFieldValues("original", fields, page);

    page.setField(Field.DESCRIPTION, null);
    fields.put(Field.DESCRIPTION, null);
    assertFieldValues("changes pre-save", fields, page);

    WorksetPage savedPage = page.clickSave();
    assertNotNull(savedPage, "Workset should save successfully");
    assertFieldValues("changes post-save", fields, savedPage);

    Workset workset = (Workset) getSession().find(Workset.class, 1L);
    assertTrue(LimsUtils.isStringEmptyOrNull(workset.getDescription()));
  }

  @Test
  public void testDuplicateAliasValidation() {
    WorksetPage page = getWorksetPage(1L);
    page.setField(Field.ALIAS, "Workset Two");
    WorksetPage savedPage = page.clickSave();
    assertNull(savedPage, "Workset fail to save");
    List<String> errs = page.getAliasValidationErrors();
    assertEquals(1, errs.size());
    assertEquals("There is already a workset with this alias", errs.get(0));
  }

  @Test
  public void testRemoveSamples() {
    WorksetPage page = getWorksetPage(1L);
    List<String> names = Lists.newArrayList("SAM100001", "SAM100002");
    List<String> samples = page.getSampleNames();
    for (String name : names) {
      assertTrue(samples.contains(name), String.format("%s should be in workset before delete", name));
    }
    WorksetPage updatedPage = page.removeSamplesByName(names);
    List<String> updated = updatedPage.getSampleNames();
    for (String name : names) {
      assertFalse(updated.contains(name), String.format("%s should be removed", name));
    }
  }

  @Test
  public void testRemoveLibraries() {
    WorksetPage page = getWorksetPage(1L);
    List<String> names = Lists.newArrayList("LIB100001", "LIB100002");
    List<String> libraries = page.getLibraryNames();
    for (String name : names) {
      assertTrue(libraries.contains(name), String.format("%s should be in workset before delete", name));
    }
    WorksetPage updatedPage = page.removeLibrariesByName(names);
    List<String> updated = updatedPage.getLibraryNames();
    for (String name : names) {
      assertFalse(updated.contains(name), String.format("%s should be removed", name));
    }
  }

  @Test
  public void testRemoveLibraryAliquots() {
    WorksetPage page = getWorksetPage(1L);
    List<String> names = Lists.newArrayList("LDI120001", "LDI120002");
    List<String> aliquotNames = page.getLibraryAliquotNames();
    for (String name : names) {
      assertTrue(aliquotNames.contains(name), String.format("%s should be in workset before delete", name));
    }
    WorksetPage updatedPage = page.removeLibraryAliquotsByName(names);
    List<String> updated = updatedPage.getLibraryAliquotNames();
    for (String name : names) {
      assertFalse(updated.contains(name), String.format("%s should be removed", name));
    }
  }

  private Map<Field, String> getWorksetOneFields() {
    Map<Field, String> fields = new HashMap<>();
    fields.put(Field.ID, "1");
    fields.put(Field.ALIAS, "Workset One");
    fields.put(Field.DESCRIPTION, "Workset One description");
    fields.put(Field.CREATOR, "admin");
    return fields;
  }

}
