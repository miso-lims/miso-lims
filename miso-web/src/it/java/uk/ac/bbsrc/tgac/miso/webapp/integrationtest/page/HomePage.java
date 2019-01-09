package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.collect.Lists;

public class HomePage extends HeaderFooterPage {

  @FindBy(css = "#tiles_project input")
  private WebElement projectSearchInput;

  @FindBy(css = "#tiles_project > div.widget")
  private WebElement projectSearchResultContainer;

  private static final By projectResultsLoadingGif = By.cssSelector("#tiles_project > div.widget > div > img");
  private static final By resultElements = By.cssSelector("div.tile");

  /**
   * Constructs a new Home Page with the assumption that the home page is already loaded or loading
   */
  public HomePage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Home"));
  }

  /**
   * Navigates to the Home Page. Will fail if not already logged in
   */
  public static HomePage get(WebDriver driver, String baseUrl) {
    driver.get(baseUrl);
    return new HomePage(driver);
  }

  /**
   * Enters the search string into the Project Search widget and returns the results
   * 
   * @param search
   * @return
   */
  public List<String> searchProjects(String search) {
    projectSearchInput.clear();
    projectSearchInput.sendKeys(search);
    projectSearchInput.sendKeys(Keys.ENTER);

    // give time for the previous results to be cleared and the loading icon to appear
    waitExplicitly(300);
    waitWithTimeout().until(invisibilityOfElementLocated(projectResultsLoadingGif));

    return getProjectSearchResults();
  }

  public List<String> getProjectSearchResults() {
    List<WebElement> results = projectSearchResultContainer.findElements(resultElements);
    List<String> projectTitles = Lists.newArrayList();
    for (WebElement result : results) {
      projectTitles.add(getProjectTitleFromSearchResult(result));
    }
    return projectTitles;
  }

  private String getProjectTitleFromSearchResult(WebElement result) {
    WebElement title = result.findElement(By.className("name"));
    return title.getText();
  }

  public ProjectPage clickProjectSearchResult(String projectTitle) {
    List<WebElement> results = projectSearchResultContainer.findElements(resultElements);
    for (WebElement result : results) {
      if (projectTitle.equals(getProjectTitleFromSearchResult(result))) {
        result.click();
        return new ProjectPage(getDriver());
      }
    }
    throw new IllegalArgumentException("No project search result found with ID " + projectTitle);
  }

}
