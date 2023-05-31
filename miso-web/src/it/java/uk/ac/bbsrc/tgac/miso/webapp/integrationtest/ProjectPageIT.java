package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage.Fields;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage.ProjectTable;

public class ProjectPageIT extends AbstractIT {

  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  @Before
  public void setup() {
    login();
  }

  private ProjectPage getProjectPage(Long projectId) {
    return ProjectPage.get(getDriver(), getBaseUrl(), projectId);
  }

  @Test
  public void testSaveNewProject() {
    // Goal: save one project.
    ProjectPage page = getProjectPage(null);
    assertNotNull(page);

    Map<String, String> unsaved = new HashMap<>();
    unsaved.put(Fields.ID, "Unsaved");
    unsaved.put(Fields.NAME, "Unsaved");
    unsaved.put(Fields.CREATION_DATE, dateFormat.format(new Date().getTime()));
    unsaved.put(Fields.TITLE, "Create New Project via UI");
    unsaved.put(Fields.CODE, "SUCHNEW");
    unsaved.put(Fields.DESCRIPTION, "New Project via UI");
    unsaved.put(Fields.STATUS, "Proposed");
    unsaved.put(Fields.REFERENCE_GENOME, "Human hg18 random");
    unsaved.put(Fields.PIPELINE, "Special");

    assertEquals("Project ID is unsaved", unsaved.get(Fields.ID), page.getId());
    assertEquals("Project name is unsaved", unsaved.get(Fields.NAME), page.getName());
    page.setTitle(unsaved.get(Fields.TITLE));
    page.setCode(unsaved.get(Fields.CODE));
    page.setDescription(unsaved.get(Fields.DESCRIPTION));
    page.setStatus(unsaved.get(Fields.STATUS));
    page.setReferenceGenome(unsaved.get(Fields.REFERENCE_GENOME));
    page.setPipeline(unsaved.get(Fields.PIPELINE));

    ProjectPage savedPage = page.clickSave();

    assertNotEquals("Project ID is now a number", unsaved.get(Fields.ID), savedPage.getId());
    assertNotEquals("Project name is saved", unsaved.get(Fields.NAME), savedPage.getName());
    assertEquals("Project creation date saved correctly", unsaved.get(Fields.CREATION_DATE),
        savedPage.getCreationDate());
    assertEquals(unsaved.get(Fields.TITLE), savedPage.getTitle());
    assertEquals(unsaved.get(Fields.CODE), savedPage.getCode());
    assertEquals(unsaved.get(Fields.DESCRIPTION), savedPage.getDescription());
    assertEquals(unsaved.get(Fields.STATUS), savedPage.getStatus());
    assertEquals(unsaved.get(Fields.REFERENCE_GENOME), savedPage.getReferenceGenome());
    assertEquals(unsaved.get(Fields.PIPELINE), savedPage.getPipeline());
  }

  @Test
  public void testEditExistingProject() {
    // Goal: change all editable fields for one project
    ProjectPage page = getProjectPage(4L);
    Map<String, String> updated = new HashMap<>();
    updated.put(Fields.TITLE, "Changed Project");
    updated.put(Fields.DESCRIPTION, "Changed Description");
    updated.put(Fields.CODE, "NEWER");
    updated.put(Fields.STATUS, "Active");
    updated.put(Fields.REFERENCE_GENOME, "Human hg19 random");

    page.setTitle(updated.get(Fields.TITLE));
    page.setCode(updated.get(Fields.CODE));
    page.setDescription(updated.get(Fields.DESCRIPTION));
    page.setStatus(updated.get(Fields.STATUS));
    page.setReferenceGenome(updated.get(Fields.REFERENCE_GENOME));

    ProjectPage savedPage = page.clickSave();

    assertEquals(updated.get(Fields.TITLE), savedPage.getTitle());
    assertEquals(updated.get(Fields.CODE), savedPage.getCode());
    assertEquals(updated.get(Fields.DESCRIPTION), savedPage.getDescription());
    assertEquals(updated.get(Fields.STATUS), savedPage.getStatus());
    assertEquals(updated.get(Fields.REFERENCE_GENOME), savedPage.getReferenceGenome());
  }

  @Test
  public void testNullifyThenFillProjectFields() {
    // Goal: empty nullable fields, then reassign values to them
    ProjectPage page = getProjectPage(1L);
    String originalDescription = page.getDescription();
    assertNotNull(originalDescription);

    page.setDescription("");
    page.clickSave();
    assertTrue(isStringEmptyOrNull(page.getDescription()));

    page.setDescription(originalDescription);
    page.clickSave();
    assertEquals(originalDescription, page.getDescription());
  }

  @Test
  public void testConfirmTablesAllVisibleWithoutErrors() throws Exception {
    // goal: ensure all tables are present on the page and have no errors
    ProjectPage page = getProjectPage(1L);

    Set<String> tableIds = Sets.newHashSet(ProjectTable.STUDIES, ProjectTable.SAMPLES, ProjectTable.LIBRARIES,
        ProjectTable.LIBRARY_ALIQUOTS, ProjectTable.POOLS,
        ProjectTable.RUNS);
    tableIds.forEach(id -> assertNotNull("table " + id + " should exist on page", page.getTable(id)));

    String errorString = page.getVisibleErrors().stream()
        .filter(error -> !error.getText().isEmpty())
        .map(error -> error.getText())
        .collect(Collectors.joining());
    assertTrue("unexpected errors on project tables: " + errorString, isStringEmptyOrNull(errorString));
  }
}
