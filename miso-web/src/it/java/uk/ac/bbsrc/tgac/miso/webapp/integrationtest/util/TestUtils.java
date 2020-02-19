package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
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
    String url = String.format("%smiso/%s", baseUrl, urlSlug);
    try {
      driver.get(url);
      // confirm that page contains logo
      if (driver.findElements(By.id("misologo")).isEmpty()) {
        log.error("/miso/{}: Page is completely empty. Is resource correct?", urlSlug);
        return true;
      }

      // check if it's an unhandled error page (JSP exception probably)
      List<WebElement> exceptionMessages = driver.findElements(By.id("exceptionMessage"));
      if (!exceptionMessages.isEmpty()) {
        log.error("/miso/{}: Stack trace on page - {}", urlSlug, stringifyMessages(exceptionMessages));
        return true;
      }

      // check if it's a handled error page (Java exception)
      List<WebElement> errorMessageElements = driver.findElements(By.id("flasherror"));
      if (!errorMessageElements.isEmpty()) {
        log.error("/miso/{}: Returned error page - {}", urlSlug, stringifyMessages(errorMessageElements));
        return true;
      }

      // check for JS errors
      LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
      for (LogEntry entry : logs) {
        if (entry.getLevel() == Level.SEVERE) {
          log.error("/miso/{}: Javascript error - {}", urlSlug, entry.getMessage());
          return true;
        }
      }

      return false;
    } catch (Exception e) {
      log.error("/miso/{}: Exception thrown while checking for errors", urlSlug, e);
      return true;
    }
  }

  private static String stringifyMessages(List<WebElement> elements) {
    return elements.stream().map(item -> item.getText().trim()).collect(Collectors.joining("\n"));
  }

}
