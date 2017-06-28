package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ProjectPage extends HeaderFooterPage {

  @FindBy(id = "projectId")
  private WebElement idLabel;

  @FindBy(id = "name")
  private WebElement nameLabel;

  public ProjectPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Project "));
  }

  public static ProjectPage get(WebDriver driver, String baseUrl, long projectId) {
    driver.get(baseUrl + "miso/project/" + projectId);
    return new ProjectPage(driver);
  }

  public String getProjectId() {
    return idLabel.getText();
  }

  public String getProjectName() {
    return nameLabel.getText();
  }

}
