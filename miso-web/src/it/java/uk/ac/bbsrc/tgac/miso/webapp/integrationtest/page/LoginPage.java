package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

  private static final String RELATIVE_URL = "login.jsp";

  private static final By LOGIN_FORM = By.id("login-form");
  private static final By USERNAME_INPUT = By.id("username");
  private static final By PASSWORD_INPUT = By.id("password");
  private static final By SUBMIT_BUTTON = By.name("login");

  private static final By FOOTER_PARAGRAPH = By.cssSelector("#footer p");

  private final WebDriver driver;
  private final Wait<WebDriver> wait;

  public LoginPage(WebDriver driver, String baseUrl) {
    this.driver = driver;
    wait = new WebDriverWait(this.driver, 10);
    this.driver.get(baseUrl + RELATIVE_URL);
    wait.until(ExpectedConditions.presenceOfElementLocated(LOGIN_FORM));
  }

  public HomePage login(String validUsername, String validPassword) {
    enterUsername(validUsername);
    enterPassword(validPassword);
    submitLogin();
    return new HomePage(driver);
  }

  private void enterUsername(String username) {
    driver.findElement(USERNAME_INPUT).sendKeys(username);
  }

  private void enterPassword(String password) {
    driver.findElement(PASSWORD_INPUT).sendKeys(password);
  }

  private void submitLogin() {
    driver.findElement(SUBMIT_BUTTON).click();
  }

  public String getFooterParagraph() {
    return driver.findElement(FOOTER_PARAGRAPH).getText();
  }

}
