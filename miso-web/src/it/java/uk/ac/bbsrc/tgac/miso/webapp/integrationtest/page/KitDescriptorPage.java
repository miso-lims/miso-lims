package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class KitDescriptorPage extends FormPage<KitDescriptorPage.Field> {


  public static enum Field implements FormPage.FieldElement {
    ID(By.id("kitDescriptorId"), FieldType.LABEL), //
    NAME(By.id("name"), FieldType.TEXT), //
    VERSION(By.id("version"), FieldType.TEXT), //
    MANUFACTURER(By.id("manufacturer"), FieldType.TEXT), //
    PART_NUMBER(By.id("partNumber"), FieldType.TEXT), //
    STOCK_LEVEL(By.id("stockLevel"), FieldType.TEXT), //
    DESCRIPTION(By.id("description"), FieldType.TEXT), //
    KIT_TYPE(By.id("kitTypes"), FieldType.DROPDOWN), //
    PLATFORM(By.id("platformTypes"), FieldType.DROPDOWN);

    private final By selector;
    private final FieldType type;

    private Field(By selector, FieldType type) {
      this.selector = selector;
      this.type = type;
    }

    @Override
    public By getSelector() {
      return selector;
    }

    @Override
    public FieldType getType() {
      return type;
    }

  } // end Field enum

  private static final String NEW_TITLE_PREFIX = "New Kit Descriptor ";

  @FindBy(id = "save")
  private WebElement saveButton;

  public KitDescriptorPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains(NEW_TITLE_PREFIX), titleContains("Kit Descriptor ")));
  }

  public static KitDescriptorPage getForCreate(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + "miso/kitdescriptor/new");
    return new KitDescriptorPage(driver);
  }

  public static KitDescriptorPage getForEdit(WebDriver driver, String baseUrl, long kitDescriptorId) {
    driver.get(baseUrl + "miso/kitdescriptor/" + kitDescriptorId);
    return new KitDescriptorPage(driver);
  }

  public KitDescriptorPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new KitDescriptorPage(getDriver());
  }

}
