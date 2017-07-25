package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.collect.Lists;

public class HandsOnTableSaveResult extends AbstractElement {

  // Assumption: these same IDs are used for all MISO HOT pages
  @FindBy(id = "successMessages")
  private WebElement successMessagesContainer;

  @FindBy(id = "serverErrors")
  private WebElement serverErrorsContainer;

  @FindBy(id = "saveErrors")
  private WebElement saveErrorsContainer;

  private static final Pattern SAVE_MESSAGE_REGEX = Pattern.compile(".*Saved (\\d+) items\\..*");

  private static final By LIST_ITEM_SELECTOR = By.cssSelector("li");

  private final int itemsSaved;
  private final List<String> serverErrors;
  private final List<String> saveErrors;

  public HandsOnTableSaveResult(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    this.itemsSaved = findSavedCount();
    this.serverErrors = Collections.unmodifiableList(findServerErrorMessages());
    this.saveErrors = Collections.unmodifiableList(findSaveErrorMessages());
  }

  private int findSavedCount() {
    Matcher m = SAVE_MESSAGE_REGEX.matcher(successMessagesContainer.getText());
    if (!m.matches()) {
      return 0;
    } else {
      return Integer.parseInt(m.group(1));
    }
  }

  private List<String> findServerErrorMessages() {
    List<String> errors = Lists.newArrayList();
    List<WebElement> elements = serverErrorsContainer.findElements(LIST_ITEM_SELECTOR);
    for (WebElement element : elements) {
      errors.add(element.getText());
    }
    return errors;
  }

  private List<String> findSaveErrorMessages() {
    List<String> errors = Lists.newArrayList();
    List<WebElement> elements = saveErrorsContainer.findElements(LIST_ITEM_SELECTOR);
    for (WebElement element : elements) {
      errors.add(element.getText());
    }
    return errors;
  }

  public int getItemsSaved() {
    return itemsSaved;
  }

  public List<String> getServerErrors() {
    return serverErrors;
  }

  public List<String> getSaveErrors() {
    return saveErrors;
  }

}
