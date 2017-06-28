package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {

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

}
