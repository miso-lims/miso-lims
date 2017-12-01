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
    loginAdmin();
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
    List<Long> badSearch = page.searchProjects("notaproject");
    assertTrue(badSearch.isEmpty());

    List<Long> goodSearch = page.searchProjects("Project One");
    assertEquals(1, goodSearch.size());
    assertEquals(1L, goodSearch.get(0).longValue());

    ProjectPage projectPage = page.clickProjectSearchResult(1L);
    assertNotNull(projectPage);
    String projectIdString = projectPage.getId();
    long projectId = Long.parseLong(projectIdString);
    assertEquals(1L, projectId);
    assertEquals("PRO1", projectPage.getName());
  }

}
