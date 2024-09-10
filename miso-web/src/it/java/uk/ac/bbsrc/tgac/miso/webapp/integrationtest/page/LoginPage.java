package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends HeaderFooterPage {

  private static final String RELATIVE_URL = "login";

  private static final By LOGIN_FORM = By.id("login-form");
  private static final By ERROR_DIV = By.className("flasherror");

  @FindBy(id = "username")
  private WebElement usernameInput;

  @FindBy(id = "password")
  private WebElement passwordInput;

  @FindBy(name = "login")
  private WebElement submitButton;

  /**
   * Constructs a new Login Page with the assumption that the login page is already loaded or loading
   */
  public LoginPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(presenceOfElementLocated(LOGIN_FORM));
  }

  /**
   * Navigates to the Login Page
   */
  public static LoginPage get(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + RELATIVE_URL);
    return new LoginPage(driver);
  }

  public HomePage loginValidUser(String username, String password) {
    submitCredentials(username, password);
    return new HomePage(getDriver());
  }

  public LoginPage loginInvalidUser(String username, String password) {
    submitCredentials(username, password);
    waitForPageRefresh(10);
    return new LoginPage(getDriver());
  }

  private void submitCredentials(String username, String password) {
    enterUsername(username);
    enterPassword(password);
    submitButton.click();
  }

  private void enterUsername(String username) {
    usernameInput.clear();
    usernameInput.sendKeys(username);
  }

  private void enterPassword(String password) {
    passwordInput.clear();
    passwordInput.sendKeys(password);
  }

  public String getErrorMessage() {
    WebElement errorDiv = findElementIfExists(ERROR_DIV);
    if (errorDiv == null) {
      return null;
    } else {
      return errorDiv.getText();
    }
  }

}
