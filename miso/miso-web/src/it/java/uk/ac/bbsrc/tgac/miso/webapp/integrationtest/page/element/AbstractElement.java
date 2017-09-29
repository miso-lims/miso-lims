package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractElement {

  private static final long DEFAULT_WAIT = 10;

  private final WebDriver driver;

  protected AbstractElement(WebDriver driver) {
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

  protected <T> void waitUntil(Function<? super WebDriver, T> condition) {
    waitUntil(condition, DEFAULT_WAIT);
  }

  protected <T> void waitUntil(Function<? super WebDriver, T> condition, long seconds) {
    waitWithTimeout(seconds).until(condition);
  }

  protected Wait<WebDriver> waitWithTimeout() {
    return new WebDriverWait(getDriver(), DEFAULT_WAIT);
  }

  protected Wait<WebDriver> waitWithTimeout(long seconds) {
    return new WebDriverWait(getDriver(), seconds);
  }

  protected WebElement findElementIfExists(By selector) {
    return findElementIfExists(getDriver(), selector);
  }

  protected static WebElement findElementIfExists(WebDriver driver, By selector) {
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

}
