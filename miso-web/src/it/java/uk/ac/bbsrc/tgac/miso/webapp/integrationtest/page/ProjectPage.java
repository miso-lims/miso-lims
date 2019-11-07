package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ProjectPage extends HeaderFooterPage {

  public static class Fields {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CREATION_DATE = "creationDate";
    public static final String ALIAS = "alias";
    public static final String SHORTNAME = "shortName";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String REFERENCE_GENOME = "referenceGenome";

    private Fields() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  }

  public static class ProjectTable {
    public static final String STUDIES = "project_studies_wrapper";
    public static final String SAMPLES = "project_samples_wrapper";
    public static final String LIBRARIES = "project_libraries_wrapper";
    public static final String LIBRARY_ALIQUOTS = "project_libraryAliquots_wrapper";
    public static final String POOLS = "project_pools_wrapper";
    public static final String RUNS = "project_runs_wrapper";
  }

  @FindBy(id = "projectForm_idLabel")
  private WebElement idLabel;
  @FindBy(id = "projectForm_nameLabel")
  private WebElement nameLabel;
  @FindBy(id = "projectForm_creationDate")
  private WebElement creationDateLabel;
  @FindBy(id = "projectForm_alias")
  private WebElement aliasLabel;
  @FindBy(id = "projectForm_shortName")
  private WebElement shortNameLabel;
  @FindBy(id = "projectForm_description")
  private WebElement descriptionLabel;
  @FindBy(id = "projectForm_status")
  private WebElement statusLabel;
  @FindBy(id = "projectForm_referenceGenomeId")
  private WebElement referenceGenomeLabel;
  @FindBy(id = "save")
  private WebElement saveButton;

  @FindBy(className = "parsley-error")
  List<WebElement> errors;

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

  public String getStatus() {
    return getSelectedDropdownText(statusLabel);
  }

  public void setStatus(String status) {
    setDropdown(status, statusLabel);
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

  public List<WebElement> getVisibleErrors() {
    return errors.stream().filter(error -> error.isDisplayed() && !LimsUtils.isStringEmptyOrNull(error.getText()))
        .collect(Collectors.toList());
  }

  public DataTable getTable(String tableWrapperId) {
    return new DataTable(getDriver(), tableWrapperId);
  }

}
