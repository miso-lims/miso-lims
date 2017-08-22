package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ProjectPage extends HeaderFooterPage {

  public static class Fields {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CREATION_DATE = "creationDate";
    public static final String ALIAS = "alias";
    public static final String SHORTNAME = "shortName";
    public static final String DESCRIPTION = "description";
    public static final String PROGRESS = "progress";
    public static final String REFERENCE_GENOME = "referenceGenome";

    private Fields() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  }

  @FindBy(id = "projectId")
  private WebElement idLabel;
  @FindBy(id = "name")
  private WebElement nameLabel;
  @FindBy(id = "creationDate")
  private WebElement creationDateLabel;
  @FindBy(id = "alias")
  private WebElement aliasLabel;
  @FindBy(id = "shortName")
  private WebElement shortNameLabel;
  @FindBy(id = "description")
  private WebElement descriptionLabel;
  @FindBy(name = "progress")
  private List<WebElement> progressLabel;
  @FindBy(id = "referenceGenome")
  private WebElement referenceGenomeLabel;
  @FindBy(id = "save")
  private WebElement saveButton;

  public ProjectPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Project "));
  }

  public static ProjectPage get(WebDriver driver, String baseUrl, Long projectId) {
    driver.get(baseUrl + "miso/project/" + (projectId == null ? "new" : projectId));
    return new ProjectPage(driver);
  }

  public String getId() {
    return idLabel.getText();
  }

  public String getName() {
    return nameLabel.getText();
  }

  public String getCreationDate() {
    return creationDateLabel.getText();
  }

  public void setCreationDate(String date) {
    setText(date, creationDateLabel);
  }

  public String getAlias() {
    return aliasLabel.getAttribute("value");
  }

  public void setAlias(String alias) {
    setText(alias, aliasLabel);
  }

  public String getShortName() {
    return shortNameLabel.getAttribute("value");
  }

  public void setShortName(String shortName) {
    setText(shortName, shortNameLabel);
  }

  public String getDescription() {
    return descriptionLabel.getAttribute("value");
  }

  public void setDescription(String description) {
    setText(description, descriptionLabel);
  }

  public String getProgress() {
    return getSelectedRadioButtonValue(progressLabel);
  }

  public void setProgress(String progress) {
    setRadioButton(progress, progressLabel);
  }

  public String getReferenceGenome() {
    return getSelectedDropdownText(referenceGenomeLabel);
  }

  public void setReferenceGenome(String referenceGenome) {
    setDropdown(referenceGenome, referenceGenomeLabel);
  }

  public ProjectPage clickSave() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new ProjectPage(getDriver());
  }
}
