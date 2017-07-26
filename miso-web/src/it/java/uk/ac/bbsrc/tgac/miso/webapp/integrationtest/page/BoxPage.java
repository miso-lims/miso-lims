package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class BoxPage extends HeaderFooterPage {

  public static class Fields {
    public static final String ID = "ID";
    public static final String ALIAS = "alias";
    public static final String DESCRIPTION = "description";
    public static final String USE = "use";
    public static final String SIZE = "size";
    public static final String LOCATION = "location";

    private Fields() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  }

  @FindBy(id = "id")
  private WebElement idLabel;
  @FindBy(id = "alias")
  private WebElement aliasLabel;
  @FindBy(id = "description")
  private WebElement descriptionLabel;
  @FindBy(id = "boxUse")
  private WebElement boxUseLabel;
  @FindBy(id = "boxSize")
  private WebElement boxSizeLabel;
  @FindBy(id = "location")
  private WebElement locationLabel;
  @FindBy(id = "save")
  private WebElement saveButton;

  public BoxPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Box"));
  }

  public static BoxPage get(WebDriver driver, String baseUrl, Long boxId) {
    driver.get(baseUrl + "miso/box/" + (boxId == null ? "new" : boxId));
    return new BoxPage(driver);
  }

  public String getId() {
    return idLabel.getText();
  }

  public String getAlias() {
    return aliasLabel.getAttribute("value");
  }

  public void setAlias(String alias) {
    setText(alias, aliasLabel);
  }

  public String getDescription() {
    return descriptionLabel.getAttribute("value");
  }

  public void setDescription(String description) {
    setText(description, descriptionLabel);
  }

  public String getBoxUse() {
    return getSelectedDropdownText(boxUseLabel);
  }

  public void setBoxUse(String boxUse) {
    setDropdown(boxUse, boxUseLabel);
  }

  public String getBoxSize() {
    if (boxSizeLabel instanceof Select) {
      return getSelectedDropdownText(boxSizeLabel);
    } else {
      return boxSizeLabel.getText();
    }
  }

  public void setBoxSize(String boxSize) {
    setDropdown(boxSize, boxSizeLabel);
  }

  public String getLocation() {
    return locationLabel.getAttribute("value");
  }

  public void setLocation(String location) {
    setText(location, locationLabel);
  }

  public void clickSave() {
    saveButton.click();
    waitWithTimeout().until(titleContains("Box "));
  }
}
