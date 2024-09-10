package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {

  private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

  private TestUtils() {
    throw new IllegalStateException("Util class not intended for instantiation");
  }

  /**
   * Checks the page for several types of errors
   * 
   * @param driver
   * @param baseUrl
   * @param urlSlug
   * @return true if an error occurs; false otherwise
   */
  public static boolean checkForErrors(WebDriver driver, String baseUrl, String urlSlug) {
    String url = String.format("%s%s", baseUrl, urlSlug);
    try {
      driver.get(url);
      return doChecks(driver, urlSlug);
    } catch (Exception e) {
      log.error("/{}: Exception thrown while checking for errors", urlSlug, e);
      return true;
    }
  }

  public static boolean checkForErrors(WebDriver driver, String baseUrl, String urlSlug,
      BiConsumer<WebDriver, String> factoryMethod) {
    try {
      factoryMethod.accept(driver, baseUrl);
      return doChecks(driver, urlSlug);
    } catch (Exception e) {
      log.error("{}: Exception thrown while checking for errors", urlSlug, e);
      return true;
    }
  }

  public static boolean checkForErrors(WebDriver driver, String baseUrl, String urlSlug, Map<String, String> formData) {
    String url = String.format("%s%s", baseUrl, urlSlug);
    try {
      postData(driver, url, formData);
      return doChecks(driver, urlSlug);
    } catch (Exception e) {
      log.error("/{}: Exception thrown while checking for errors", urlSlug, e);
      return true;
    }
  }

  public static boolean checkCurrentPageForErrors(WebDriver driver) {
    return doChecks(driver, null);
  }

  private static boolean doChecks(WebDriver driver, String urlSlug) {
    String url = null;
    if (urlSlug == null) {
      url = driver.getCurrentUrl();
    } else {
      url = urlSlug;
      if (!driver.getCurrentUrl().contains(url)) {
        log.error("{}: Navigation failed", url);
        return true;
      }
    }

    // confirm that page contains logo
    try {
      new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.id("misologo")));
    } catch (TimeoutException e) {
      log.error("{}: MISO logo image not found. Is resource correct?", url);
      return true;
    }

    // check if it's an unhandled error page (JSP exception probably)
    List<WebElement> exceptionMessages = driver.findElements(By.id("exceptionMessage"));
    if (!exceptionMessages.isEmpty()) {
      log.error("{}: Stack trace on page - {}", url, stringifyMessages(exceptionMessages));
      return true;
    }

    // check if it's a handled error page (Java exception)
    List<WebElement> errorMessageElements = driver.findElements(By.id("flasherror"));
    if (!errorMessageElements.isEmpty()) {
      log.error("{}: Returned error page - {}", url, stringifyMessages(errorMessageElements));
      return true;
    }

    // check for JS errors
    LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
    for (LogEntry entry : logs) {
      if (entry.getLevel() == Level.SEVERE) {
        log.error("{}: Javascript error - {}", url, entry.getMessage());
        return true;
      }
    }

    return false;
  }

  private static String stringifyMessages(List<WebElement> elements) {
    return elements.stream().map(item -> item.getText().trim()).collect(Collectors.joining("\n"));
  }

  public static void postData(WebDriver driver, String url, Map<String, String> parameters) {
    StringBuilder sb = new StringBuilder("Utils.page.post('")
        .append(url)
        .append("', {")
        .append(parameters.entrySet().stream().map(entry -> entry.getKey() + ": '" + entry.getValue() + "'")
            .collect(Collectors.joining(", ")))
        .append("});");
    ((JavascriptExecutor) driver).executeScript(sb.toString());
  }

  public static String qcPassedToString(Boolean qcStatus) {
    if (qcStatus == null) {
      return "Not Ready";
    } else if (qcStatus) {
      return "Ready";
    } else {
      return "Failed";
    }
  }

}
