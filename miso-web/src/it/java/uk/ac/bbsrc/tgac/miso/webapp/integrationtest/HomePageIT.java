package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.HomePage;

@Ignore
public class HomePageIT extends AbstractIT {

  private HomePage page;

  @Before
  public void setup() {
    loginAdmin();
    page = HomePage.get(getDriver(), getBaseUrl());
    assertNotNull(page);
  }

  @Test
  public void testProjectSearch() {
    List<Long> badSearch = page.searchProjects("asdf");
    assertTrue(badSearch.isEmpty());
  }

}
