package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.HomePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage;

public class HomePageIT extends AbstractIT {

  private HomePage page;

  @Before
  public void setup() {
    login();
    page = HomePage.get(getDriver(), getBaseUrl());
    assertNotNull(page);
  }

  @Test
  public void testVersion() {
    String text = page.getFooterText();
    assertNotNull(text);
    assertTrue("Version number in footer", text.matches(".*Version: .*\\d+\\.\\d+\\.\\d+.*"));
  }

  @Test
  public void testProjectSearch() {
    List<String> badSearch = page.searchProjects("notaproject");
    assertTrue(badSearch.isEmpty());

    List<String> goodSearch = page.searchProjects("Project One");
    assertEquals(1, goodSearch.size());
    assertEquals("PRO1", goodSearch.get(0));

    ProjectPage projectPage = page.clickProjectSearchResult("PRO1");
    assertNotNull(projectPage);
    String projectIdString = projectPage.getId();
    long projectId = Long.parseLong(projectIdString);
    assertEquals(1L, projectId);
    assertEquals("PRO1", projectPage.getName());
  }

}
