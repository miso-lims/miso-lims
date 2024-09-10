package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class StudyPage extends HeaderFooterPage {

  public static class Fields {
    public static final String ID = "id";
    public static final String PROJECT = "projectId";
    public static final String NAME = "name";
    public static final String ALIAS = "alias";
    public static final String DESCRIPTION = "description";
    public static final String STUDY_TYPE = "studyTypeId";

    private Fields() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  }

  @FindBy(id = "studyForm_idLabel")
  private WebElement idLabel;
  @FindBy(id = "studyForm_projectId")
  private WebElement projectLabel;
  @FindBy(id = "studyForm_nameLabel")
  private WebElement nameLabel;
  @FindBy(id = "studyForm_alias")
  private WebElement aliasLabel;
  @FindBy(id = "studyForm_description")
  private WebElement descriptionLabel;
  @FindBy(id = "studyForm_studyTypeId")
  private WebElement studyTypeLabel;
  @FindBy(id = "save")
  private WebElement saveButton;

  public StudyPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Study "));
  }

  public static StudyPage get(WebDriver driver, String baseUrl, Long studyId, Long projectId) {
    if (studyId == null && projectId == null)
      throw new IllegalArgumentException("Project ID cannot be null when creating a new study");
    driver.get(baseUrl + "study/" + (studyId == null ? "new" : studyId) + (projectId == null ? "" : "/" + projectId));
    return new StudyPage(driver);
  }

  public String getId() {
    return idLabel.getText();
  }

  public String getProject() {
    if ("select".equals(projectLabel.getTagName())) {
      return getSelectedDropdownText(projectLabel);
    } else {
      return getDriver().findElement(By.id("studyForm_projectIdLabel")).getText();
    }
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
