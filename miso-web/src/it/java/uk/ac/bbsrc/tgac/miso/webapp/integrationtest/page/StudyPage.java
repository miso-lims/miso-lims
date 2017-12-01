package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class StudyPage extends HeaderFooterPage {

  public static class Fields {
    public static final String ID = "id";
    public static final String PROJECT = "projectName";
    public static final String NAME = "name";
    public static final String ALIAS = "alias";
    public static final String DESCRIPTION = "description";
    public static final String STUDY_TYPE = "studyType";

    private Fields() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  }

  @FindBy(id = "studyId")
  private WebElement idLabel;
  @FindBy(id = "projectName")
  private WebElement projectLabel;
  @FindBy(id = "name")
  private WebElement nameLabel;
  @FindBy(id = "alias")
  private WebElement aliasLabel;
  @FindBy(id = "description")
  private WebElement descriptionLabel;
  @FindBy(id = "studyType")
  private WebElement studyTypeLabel;
  @FindBy(id = "save")
  private WebElement saveButton;

  public StudyPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Study "));
  }

  public static StudyPage get(WebDriver driver, String baseUrl, Long studyId, Long projectId) {
    if (studyId == null && projectId == null) throw new IllegalArgumentException("Project ID cannot be null when creating a new study");
    driver.get(baseUrl + "miso/study/" + (studyId == null ? "new" : studyId) + (projectId == null ? "" : "/" + projectId));
    return new StudyPage(driver);
  }

  public String getId() {
    return idLabel.getText();
  }

  public String getProject() {
    return projectLabel.getText();
  }

  public String getName() {
    return nameLabel.getText();
  }

  public void setName(String name) {
    setText(name, nameLabel);
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

  public String getStudyType() {
    return getSelectedDropdownText(studyTypeLabel);
  }

  public void setStudyType(String studyType) {
    setDropdown(studyType, studyTypeLabel);
  }

  public StudyPage clickSave() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new StudyPage(getDriver());
  }

}
