package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.HomePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage;

public class HomePageIT extends AbstractIT {

  private HomePage page;

  @BeforeEach
  public void setup() {
    page = login();
    assertNotNull(page);
  }

  @Test
  public void testVersion() {
    String text = page.getFooterText();
    assertNotNull(text);
    assertTrue(text.matches(".*Version: .*\\d+\\.\\d+\\.\\d+.*"), "Version number in footer");
  }

  @Test
  public void testProjectSearch() {
    List<String> badSearch = page.searchProjects("notaproject");
    assertTrue(badSearch.isEmpty());

    List<String> goodSearch = page.searchProjects("Project One");
    assertEquals(1, goodSearch.size());
    assertEquals("PONE", goodSearch.get(0));

    ProjectPage projectPage = page.clickProjectSearchResult("PONE");
    assertNotNull(projectPage);
    String projectIdString = projectPage.getId();
    long projectId = Long.parseLong(projectIdString);
    assertEquals(1L, projectId);
    assertEquals("PRO1", projectPage.getName());
  }

}
