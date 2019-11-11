package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.utils.Sets;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.MoreExpectedConditions;

/**
 * Page that is mainly composed of a list of fields
 */
public abstract class FormPage<T extends FormPage.FieldElement> extends HeaderFooterPage {

  private static final Set<String> editableTags = Sets.newHashSet("select", "textarea");
  private static final Set<String> editableInputTypes = Sets.newHashSet("text", "checkbox");

  public interface FieldElement {
    public By getSelector();

    public default By getLabelSelector() {
      return null;
    }

    public default String get(WebDriver driver) {
      WebElement element = null;
      if (getLabelSelector() != null) {
        element = findElementIfExists(driver, getLabelSelector());
      }
      if (element == null) {
        element = driver.findElement(getSelector());
      }
      switch (element.getTagName()) {
      case "td":
      case "span":
      case "p":
      case "a":
      case "div":
        return element.getText();
      case "select":
        return getSelectedDropdownText(element);
      case "textarea":
        return element.getAttribute("value");
      case "input":
        switch (element.getAttribute("type")) {
        case "text":
        case "hidden":
          return element.getAttribute("value");
        case "checkbox":
          // "true" or "false"
          return element.isSelected() ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
        case "radio":
          return getSelectedRadioButtonValue(driver.findElements(getSelector()));
        default:
          throw new IllegalArgumentException("Unhandled input type: " + element.getAttribute("type"));
        }
      default:
        throw new IllegalArgumentException("Unhandled element type: " + element.getTagName());
      }
    }

    public default void set(WebDriver driver, String value) {
      WebElement element = driver.findElement(getSelector());
      switch (element.getTagName()) {
      case "input":
        switch (element.getAttribute("type")) {
        case "text":
          setText(value, element);
          if (element.getAttribute("className").contains("hasDatepicker")) {
            ((JavascriptExecutor) driver).executeScript("jQuery('.ui-datepicker').hide();");
          }
          break;
        case "checkbox":
          if (!value.equalsIgnoreCase(Boolean.TRUE.toString()) && !value.equalsIgnoreCase(Boolean.FALSE.toString())) {
            throw new IllegalArgumentException("Checkbox value must be 'true' or 'false'");
          }
          setCheckbox(Boolean.valueOf(value), element);
          break;
        default:
          throw new IllegalArgumentException("Unhandled input type: " + element.getAttribute("type"));
        }
        break;
      case "select":
        setDropdown(value, element);
        break;
      case "textarea":
        setText(value, element);
        break;
      default:
        throw new IllegalArgumentException("Unhandled element type: " + element.getTagName());
      }
    }

    public default boolean isEditable(WebDriver driver) {
      WebElement element = findElementIfExists(driver, getSelector());
      if (element == null || !element.isDisplayed() || !element.isEnabled() || element.getAttribute("readonly") != null) {
        return false;
      } else {
        return editableTags.contains(element.getTagName())
            || (element.getTagName().equals("input") && editableInputTypes.contains(element.getAttribute("type")));
      }
    }
  }

  public FormPage(WebDriver driver, String formId) {
    super(driver);
    waitUntil(MoreExpectedConditions.jsReturnsTrue(String.format("return FormUtils.isInitialized('%s');", formId)));
  }

  public boolean isEditable(T field) {
    return field.isEditable(getDriver());
  }

  public String getField(T field) {
    return field.get(getDriver());
  }

  public void setField(T field, String value) {
    field.set(getDriver(), value);
  }

  public void setFields(Map<T, String> fields) {
    fields.forEach((key, val) -> setField(key, val));
  }

  public void printValidationErrors(String formId) {
    errLog("General errors:");
    List<WebElement> generalErrors = getDriver().findElements(By.cssSelector("#" + formId + " .generalErrors li"));
    if (generalErrors.isEmpty()) {
      errLog("(None)");
    } else {
      for (WebElement generalError : generalErrors) {
        errLog("* " + generalError.getText());
      }
    }
    errLog("Field errors:");
    List<WebElement> errorContainers = getDriver().findElements(By.className(".errorContainer"));
    boolean fieldErrors = false;
    for (WebElement errorContainer : errorContainers) {
      List<WebElement> errors = errorContainer.findElements(By.tagName("LI"));
      if (!errors.isEmpty()) {
        fieldErrors = true;
        for (WebElement error : errors) {
          errLog(String.format("* %s: %s", error.getAttribute("id"), error.getText()));
        }
      }
    }
    if (!fieldErrors) {
      errLog("(None)");
    }
  }

  private void errLog(String message) {
    System.err.println(message);
  }

}
