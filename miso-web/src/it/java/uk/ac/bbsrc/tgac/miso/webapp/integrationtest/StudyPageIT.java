package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.StudyPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.StudyPage.Fields;

public class StudyPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  private StudyPage getStudyPage(Long studyId, Long projectId) {
    return StudyPage.get(getDriver(), getBaseUrl(), studyId, projectId);
  }

  @Test
  public void testSaveNewStudy() {
    // Goal: save one study
    StudyPage page = getStudyPage(null, 400L);
    assertNotNull(page);

    Map<String, String> unsaved = new HashMap<>();
    unsaved.put(Fields.ID, "Unsaved");
    unsaved.put(Fields.PROJECT, "PRO400");
    unsaved.put(Fields.NAME, "Unsaved");
    unsaved.put(Fields.ALIAS, "Create new Study via UI");
    unsaved.put(Fields.DESCRIPTION, "New Study via UI");
    unsaved.put(Fields.STUDY_TYPE, "Other");

    assertEquals("Study ID is unsaved", unsaved.get(Fields.ID), page.getId());
    assertEquals("Study name is unsaved", unsaved.get(Fields.NAME), page.getName());
    assertEquals("Project name is set", unsaved.get(Fields.PROJECT), page.getProject());
    page.setAlias(unsaved.get(Fields.ALIAS));
    page.setDescription(unsaved.get(Fields.DESCRIPTION));
    page.setStudyType(unsaved.get(Fields.STUDY_TYPE));

    StudyPage saved = page.clickSave();

    assertNotEquals("Study ID is now a number", unsaved.get(Fields.ID), saved.getId());
    assertNotEquals("Study name is saved", unsaved.get(Fields.NAME), saved.getName());
    assertEquals("Project name is same", unsaved.get(Fields.PROJECT), saved.getProject());
    assertEquals(unsaved.get(Fields.ALIAS), saved.getAlias());
    assertEquals(unsaved.get(Fields.DESCRIPTION), saved.getDescription());
    assertEquals(unsaved.get(Fields.STUDY_TYPE), saved.getStudyType());
  }

  @Test
  public void testEditExistingStudy() {
    // Goal: change all editable fields for one study
    StudyPage page = getStudyPage(400L, null);
    Map<String, String> updated = new HashMap<>();
    updated.put(Fields.ALIAS, "Changed Study");
    updated.put(Fields.DESCRIPTION, "Changed Description");
    updated.put(Fields.STUDY_TYPE, "Cancer Genomics");

    page.setAlias(updated.get(Fields.ALIAS));
    page.setDescription(updated.get(Fields.DESCRIPTION));
    page.setStudyType(updated.get(Fields.STUDY_TYPE));

    StudyPage savedPage = page.clickSave();

    assertEquals(updated.get(Fields.ALIAS), savedPage.getAlias());
    assertEquals(updated.get(Fields.DESCRIPTION), savedPage.getDescription());
    assertEquals(updated.get(Fields.STUDY_TYPE), savedPage.getStudyType());
  }

  @Test
  public void testNullifyThenFillStudyFields() {
    // Goal: empty nullable field, then reassign values to it
    StudyPage page = getStudyPage(400L, null);
    String originalDescription = page.getDescription();
    assertNotNull(originalDescription);

    page.setDescription("");
    StudyPage savedOnce = page.clickSave();
    assertTrue(isStringEmptyOrNull(savedOnce.getDescription()));

    page.setDescription(originalDescription);
    StudyPage savedTwice = page.clickSave();
    assertEquals(originalDescription, savedTwice.getDescription());
  }
}
