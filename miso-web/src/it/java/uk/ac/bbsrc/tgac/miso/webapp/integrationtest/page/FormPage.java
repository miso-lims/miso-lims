package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.Map;

import org.openqa.selenium.WebDriver;

/**
 * Page that is mainly composed of a list of fields
 * 
 * @param <T> field enum
 */
public abstract class FormPage<T> extends HeaderFooterPage {

  public FormPage(WebDriver driver) {
    super(driver);
  }

  public abstract boolean isEditable(T field);

  public abstract String getField(T field);

  public abstract void setField(T field, String value);

  public void setFields(Map<T, String> fields) {
    fields.forEach((key, val) -> setField(key, val));
  }

}
