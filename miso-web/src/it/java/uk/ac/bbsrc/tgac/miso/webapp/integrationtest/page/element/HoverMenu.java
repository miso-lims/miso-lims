package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class HoverMenu extends AbstractElement {

  private static final Pattern openMenuFunction = Pattern.compile(".*mopen\\(\\'(.*)\\'\\).*");
  private static final By optionsSelector = By.tagName("a");

  private final WebElement menu;
  private final WebElement optionsDiv;

  public HoverMenu(WebDriver driver, By selector) {
    super(driver);
    menu = driver.findElement(selector);
    String onmouseover = menu.getAttribute("onmouseover");
    Matcher m = openMenuFunction.matcher(onmouseover);
    if (!m.matches()) {
      throw new IllegalStateException("The element found using this selector does not seem to be a hover menu");
    }
    String optionsDivId = m.group(1);
    optionsDiv = driver.findElement(By.id(optionsDivId));
  }

  public void clickOption(String option) {
    open();
    for (WebElement element : getOptionElements()) {
      if (option.equals(element.getText())) {
        element.click();
        return;
      }
    }
    throw new IllegalArgumentException("Option not found");
  }
  
  private void open() {
    if (!optionsDiv.isDisplayed()) {
      new Actions(getDriver()).moveToElement(menu).build().perform();
    }
    waitUntil(visibilityOf(optionsDiv));
  }

  private List<WebElement> getOptionElements() {
    return optionsDiv.findElements(optionsSelector);
  }

}
