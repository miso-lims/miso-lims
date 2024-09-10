package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class KitDescriptorPage extends FormPage<KitDescriptorPage.Field> {


  public static enum Field implements FormPage.FieldElement {
    ID(By.id("kitDescriptorForm_idLabel")), //
    NAME(By.id("kitDescriptorForm_name")), //
    VERSION(By.id("kitDescriptorForm_version")), //
    MANUFACTURER(By.id("kitDescriptorForm_manufacturer")), //
    PART_NUMBER(By.id("kitDescriptorForm_partNumber")), //
    STOCK_LEVEL(By.id("kitDescriptorForm_stockLevel")), //
    DESCRIPTION(By.id("kitDescriptorForm_description")), //
    KIT_TYPE(By.id("kitDescriptorForm_kitType")), //
    PLATFORM(By.id("kitDescriptorForm_platformType"));

    private final By selector;

    private Field(By selector) {
      this.selector = selector;
    }

    @Override
    public By getSelector() {
      return selector;
    }

  } // end Field enum

  private static final String NEW_TITLE_PREFIX = "New Kit Descriptor ";

  @FindBy(id = "save")
  private WebElement saveButton;

  public KitDescriptorPage(WebDriver driver) {
    super(driver, "kitDescriptorForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains(NEW_TITLE_PREFIX), titleContains("Kit Descriptor ")));
  }

  public static KitDescriptorPage getForCreate(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + "kitdescriptor/new");
    return new KitDescriptorPage(driver);
  }

  public static KitDescriptorPage getForEdit(WebDriver driver, String baseUrl, long kitDescriptorId) {
    driver.get(baseUrl + "kitdescriptor/" + kitDescriptorId);
    return new KitDescriptorPage(driver);
  }

  public KitDescriptorPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new KitDescriptorPage(getDriver());
  }

}
