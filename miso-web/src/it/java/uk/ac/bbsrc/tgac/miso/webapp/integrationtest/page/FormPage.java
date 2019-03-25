package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page that is mainly composed of a list of fields
 */
public abstract class FormPage<T extends FormPage.FieldElement> extends HeaderFooterPage {

  public interface FieldElement {
    public By getSelector();

    public default By getLabelSelector() {
      return null;
    }

    public FieldType getType();

    public default boolean isEditable(WebDriver driver) {
      return getType().isEditable(driver, getSelector());
    }

    public default String get(WebDriver driver) {
      WebElement element = driver.findElement(getSelector());
      switch (element.getTagName()) {
      case "td":
      case "span":
      case "p":
      case "a":
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
      if (isEditable(driver)) {
        getType().setValue(driver, getSelector(), value);
      } else {
        throw new IllegalStateException(this.toString() + " is read-only");
      }
    }
  }

  public FormPage(WebDriver driver) {
    super(driver);
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

}
