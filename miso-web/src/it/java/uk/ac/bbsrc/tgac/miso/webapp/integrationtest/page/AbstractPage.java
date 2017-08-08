package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.AbstractElement;

public abstract class AbstractPage extends AbstractElement {

  private static final ExpectedCondition<Boolean> pageLoaded = (driver) -> {
    return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
  };

  protected AbstractPage(WebDriver driver) {
    super(driver);
  }

  protected static void waitExplicitly(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException("Thread interrupted during sleep", e);
    }
  }

  /**
   * Assesses page refresh by checking the staleness state of the HTML tag of the current page.
   * 
   * @param html the HTML element of the current page
   */
  protected void waitForPageRefresh(WebElement html) {
    waitUntil(ExpectedConditions.stalenessOf(html));
    waitUntil(pageLoaded);
  }

  protected void waitForPageRefresh(long seconds) {
    waitUntil(pageLoaded, seconds);
  }

  protected void setText(String input, WebElement element) {
    element.click();
    element.clear();
    element.sendKeys(input);
    element.sendKeys(Keys.ESCAPE);
  }

  protected void setCheckbox(Boolean check, WebElement element) {
    // only change if given value and element value differ
    if (check && !element.isSelected()) {
      element.click();
    }
    if (!check && element.isSelected()) {
      element.click();
    }
  }

  protected void setDropdown(String input, WebElement element) {
    element.click();
    Select select = new Select(element);
    select.selectByVisibleText(input);
    element.sendKeys(Keys.ESCAPE);
  }

  protected String getSelectedDropdownText(WebElement element) {
    Select dropdown = new Select(element);
    return dropdown.getFirstSelectedOption().getText();
  }

  protected void setRadioButton(String input, List<WebElement> buttons) {
    WebElement targetButton = buttons.stream().filter(button -> button.getAttribute("value").equals(input)).findAny().orElse(null);
    if (targetButton == null) throw new IllegalArgumentException("Could not find radio button with label " + input);
    targetButton.click();
  }

  protected String getSelectedRadioButtonText(List<WebElement> buttons) {
    WebElement selectedButton = buttons.stream().filter(button -> button.isSelected()).findAny().orElse(null);
    if (selectedButton == null) throw new IllegalArgumentException("No buttons are selected for set " + buttons.get(0).getAttribute("id"));
    return selectedButton.getAttribute("value");
  }

  private static final String MISO_URL = "%smiso/%s";
  private static final String MISO_STACKTRACE = "uk.ac.bbsrc";

  public static boolean checkForErrors(WebDriver driver, String baseUrl, String urlSlug) {
    String url = String.format(MISO_URL, baseUrl, urlSlug);
    driver.get(url);
    // confirm that page contains logo
    if (driver.findElements(By.id("misologo")).isEmpty())
      throw new IllegalArgumentException("Page at /miso/" + urlSlug + " is completely empty. Is resource correct?");

    List<WebElement> errors = driver.findElements(By.xpath("//li[contains(text(), '" + MISO_STACKTRACE + "')]"));
    if (errors.size() > 0) {
      errors.stream().map(item -> item.getText().trim()).collect(Collectors.toList()).toString();
      return true;
    } else {
      return false;
    }
  }

  protected WebElement getHtmlElement() {
    return getDriver().findElement(By.tagName("html"));
  }

}
