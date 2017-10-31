package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage.Fields;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage.ProjectTable;

public class ProjectPageIT extends AbstractIT {

  static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

  @Before
  public void setup() {
    loginAdmin();
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
    unsaved.put(Fields.CREATION_DATE, dateFormatter.print(new Date().getTime()));
    unsaved.put(Fields.ALIAS, "Create New Project via UI");
    unsaved.put(Fields.SHORTNAME, "NEW");
    unsaved.put(Fields.DESCRIPTION, "New Project via UI");
    unsaved.put(Fields.PROGRESS, "PROPOSED");
    unsaved.put(Fields.REFERENCE_GENOME, "Human hg18 random");
    
    assertEquals("Project ID is unsaved", unsaved.get(Fields.ID), page.getId());
    assertEquals("Project name is unsaved", unsaved.get(Fields.NAME), page.getName());
    assertEquals("Project creation date is set to today", unsaved.get(Fields.CREATION_DATE), page.getCreationDate());
    page.setAlias(unsaved.get(Fields.ALIAS));
    page.setShortName(unsaved.get(Fields.SHORTNAME));
    page.setDescription(unsaved.get(Fields.DESCRIPTION));
    page.setProgress(unsaved.get(Fields.PROGRESS));
    page.setReferenceGenome(unsaved.get(Fields.REFERENCE_GENOME));

    ProjectPage savedPage = page.clickSave();

    assertNotEquals("Project ID is now a number", unsaved.get(Fields.ID), savedPage.getId());
    assertNotEquals("Project name is saved", unsaved.get(Fields.NAME), savedPage.getName());
    assertEquals("Project creation date saved correctly", unsaved.get(Fields.CREATION_DATE), savedPage.getCreationDate());
    assertEquals(unsaved.get(Fields.ALIAS), savedPage.getAlias());
    assertEquals(unsaved.get(Fields.SHORTNAME), savedPage.getShortName());
    assertEquals(unsaved.get(Fields.DESCRIPTION), savedPage.getDescription());
    assertEquals(unsaved.get(Fields.PROGRESS), savedPage.getProgress());
    assertEquals(unsaved.get(Fields.REFERENCE_GENOME), savedPage.getReferenceGenome());
  }

  @Test
  public void testEditExistingProject() {
    // Goal: change all editable fields for one project
    ProjectPage page = getProjectPage(4L);
    Map<String, String> updated = new HashMap<>();
    updated.put(Fields.ALIAS, "Changed Project");
    updated.put(Fields.DESCRIPTION, "Changed Description");
    updated.put(Fields.SHORTNAME, "NEWER");
    updated.put(Fields.PROGRESS, "ACTIVE");
    updated.put(Fields.REFERENCE_GENOME, "Human hg19 random");

    page.setAlias(updated.get(Fields.ALIAS));
    page.setShortName(updated.get(Fields.SHORTNAME));
    page.setDescription(updated.get(Fields.DESCRIPTION));
    page.setProgress(updated.get(Fields.PROGRESS));
    page.setReferenceGenome(updated.get(Fields.REFERENCE_GENOME));

    ProjectPage savedPage = page.clickSave();

    assertEquals(updated.get(Fields.ALIAS), savedPage.getAlias());
    assertEquals(updated.get(Fields.SHORTNAME), savedPage.getShortName());
    assertEquals(updated.get(Fields.DESCRIPTION), savedPage.getDescription());
    assertEquals(updated.get(Fields.PROGRESS), savedPage.getProgress());
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
    
    Set<String> tableIds = Sets.newHashSet(ProjectTable.STUDIES, ProjectTable.SAMPLES, ProjectTable.LIBRARIES, ProjectTable.DILUTIONS, ProjectTable.POOLS,
        ProjectTable.RUNS);
    tableIds.forEach(id -> assertNotNull("table " + id + " should exist on page", page.getTable(id)));

    String errorString = page.getVisibleErrors().stream()
        .filter(error -> !error.getText().isEmpty())
        .map(error -> error.getText())
        .collect(Collectors.joining());
    assertTrue("unexpected errors on project tables: " + errorString, isStringEmptyOrNull(errorString));
  }

  @Test
  public void testAddOverview() {
    // goal: ensure that one project overview can be added to a project
    final String investigator = "well-dressed crocodilian";
    final Integer numSamples = 3;

    ProjectPage page = getProjectPage(900L);
    page.addOverview(investigator, numSamples);

    Project project = (Project) getSession().get(ProjectImpl.class, 900L);
    assertEquals(1, project.getOverviews().size());
    project.getOverviews().stream().forEach(pov -> {
      assertEquals(investigator, pov.getPrincipalInvestigator());
      assertEquals(numSamples, pov.getNumProposedSamples());
    });
  }
}
