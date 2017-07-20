package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractPage {

  private static final long DEFAULT_WAIT = 15;

  private final WebDriver driver;

  private static final ExpectedCondition<Boolean> pageLoaded = (driver) -> {
    return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
  };

  protected AbstractPage(WebDriver driver) {
    this.driver = driver;
  }

  protected WebDriver getDriver() {
    return driver;
  }

  protected static void waitExplicitly(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException("Thread interrupted during sleep", e);
    }
  }

  protected void waitForPageRefresh() {
    waitUntil(pageLoaded, DEFAULT_WAIT);
  }

  protected void waitForPageRefresh(long seconds) {
    waitUntil(pageLoaded, seconds);
  }

  private void waitUntil(Function<? super WebDriver, Boolean> condition, long seconds) {
    waitWithTimeout(seconds).until(condition);
  }

  protected Wait<WebDriver> waitWithTimeout() {
    return new WebDriverWait(getDriver(), DEFAULT_WAIT);
  }

  protected Wait<WebDriver> waitWithTimeout(long seconds) {
    return new WebDriverWait(getDriver(), seconds);
  }

  protected WebElement findElementIfExists(By selector) {
    List<WebElement> elements = driver.findElements(selector);
    switch (elements.size()) {
    case 0:
      return null;
    case 1:
      return elements.get(0);
    default:
      throw new InvalidArgumentException("Selector yielded multiple elements. Use WebDriver#findElements instead");
    }
  }

  protected void setText(String input, WebElement element) {
    element.click();
    element.clear();
    element.sendKeys(input);
    element.sendKeys(Keys.ESCAPE);
  }

  protected void setDropdown(String input, WebElement element) {
    element.click();
    ((Select) element).selectByVisibleText(input);
    element.sendKeys(Keys.ESCAPE);
  }

  protected void setCheckbox(Boolean value, WebElement element) {
    // only change if given value and element value differ
    if (value && "false".equals(element.getAttribute("value"))) {
      element.click();
    }
    if (!value && "true".equals(element.getAttribute("value"))) {
      element.click();
    }
  }

  protected String getSelectedDropdownText(WebElement element) {
    Select dropdown = new Select(element);
    return dropdown.getFirstSelectedOption().getText();
  }
}
