package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page that is mainly composed of a list of fields
 */
public abstract class FormPage<T extends FormPage.FieldElement> extends HeaderFooterPage {

  public interface FieldElement {
    public By getSelector();

    public FieldType getType();

    public default boolean isEditable(WebDriver driver) {
      return getType().isEditable(driver, getSelector());
    }

    public default String get(WebDriver driver) {
      return getType().getValue(driver, getSelector());
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
