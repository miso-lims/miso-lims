package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class MoreExpectedConditions {

  private MoreExpectedConditions() {
    throw new IllegalStateException("Util class not intended for instantiation");
  }

  public static ExpectedCondition<Boolean> textDoesNotContain(WebElement element, String text) {
    return (driver) -> !element.getText().contains(text);
  }

}
