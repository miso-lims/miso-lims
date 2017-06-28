package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.collect.Lists;

public class HomePage {

  private static final By projectSearchInput = By.id("searchProject");
  private static final By projectSearchResultContainer = By.id("searchProjectresult");
  private static final By resultsLoadingGif = By.tagName("img"); // nested in a SearchResultContainer while loading
  private static final By resultElements = By.cssSelector("a.dashboardresult"); // nested in SearchResultContainer

  private final WebDriver driver;
  private final Wait<WebDriver> wait;

  /**
   * Constructs a new Home Page with the assumption that the home page is already loaded or loading
   */
  public HomePage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(this.driver, 10);
    wait.until(ExpectedConditions.titleContains("Home"));
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
  public List<Long> searchProjects(String search) {
    driver.findElement(projectSearchInput).sendKeys(search);
    WebElement resultContainer = driver.findElement(projectSearchResultContainer);
    wait.until(presenceOfNestedElementLocatedBy(projectSearchResultContainer, resultsLoadingGif));
    wait.until(not(presenceOfNestedElementLocatedBy(resultContainer, resultsLoadingGif)));
    return getProjectSearchResults();
  }

  public List<Long> getProjectSearchResults() {
    List<WebElement> results = getProjectSearchResultElements();
    List<Long> projectIds = Lists.newArrayList();
    for (WebElement result : results) {
      projectIds.add(getProjectIdFromSearchResult(result));
    }
    return projectIds;
  }

  // TODO public ProjectPage clickProjectSearchResult(Long projectId) {}

  private List<WebElement> getProjectSearchResultElements() {
    WebElement resultContainer = driver.findElement(projectSearchResultContainer);
    return resultContainer.findElements(resultElements);
  }

  private Long getProjectIdFromSearchResult(WebElement result) {
    String linkUrl = result.getAttribute("href");
    return Long.valueOf(linkUrl.replaceFirst("/miso/project/", ""));
  }

}
