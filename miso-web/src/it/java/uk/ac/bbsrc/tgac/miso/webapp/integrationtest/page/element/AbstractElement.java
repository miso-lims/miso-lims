package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.MoreExpectedConditions;

public abstract class AbstractElement {

  private static final long DEFAULT_WAIT = 10;
  private static final Map<String, String> CHAR_FIXES;

  static {
    CHAR_FIXES = Maps.newHashMap();
    CHAR_FIXES.put("0", "\u0030");
    CHAR_FIXES.put("1", "\u0031");
    CHAR_FIXES.put("2", "\u0032");
    CHAR_FIXES.put("3", "\u0033");
    CHAR_FIXES.put("4", "\u0034");
    CHAR_FIXES.put("5", "\u0035");
    CHAR_FIXES.put("6", "\u0036");
    CHAR_FIXES.put("7", "\u0037");
    CHAR_FIXES.put("8", "\u0038");
    CHAR_FIXES.put("9", "\u0039");
  }

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

  protected void safeInput(By selector, String input) {
    WebElement element = getDriver().findElement(selector);
    for (int i = 0; i < input.length(); i++) {
      String currentChar = Character.toString(input.charAt(i));
      if (CHAR_FIXES.containsKey(currentChar)) {
        element.sendKeys(CHAR_FIXES.get(currentChar));
      } else {
        element.sendKeys(currentChar);
      }
      waitUntil(MoreExpectedConditions.textHasLength(selector, i + 1));
    }
  }

}
